package com.stoneridge.backend.service;

import com.plaid.client.model.*;
import com.plaid.client.request.PlaidApi;
import com.stoneridge.backend.dto.BankDTO;
import com.stoneridge.backend.exception.BadRequestException;
import com.stoneridge.backend.exception.ResourceNotFoundException;
import com.stoneridge.backend.model.Bank;
import com.stoneridge.backend.model.User;
import com.stoneridge.backend.repository.BankRepository;
import com.stoneridge.backend.repository.UserRepository;
import com.stoneridge.backend.util.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BankService {

    private static final Logger log = LoggerFactory.getLogger(BankService.class);
    private final PlaidApi plaidApi;
    private final BankRepository bankRepository;
    private final UserRepository userRepository;
    private final DwollaService dwollaService;

    public BankService(PlaidApi plaidApi, BankRepository bankRepository, UserRepository userRepository, DwollaService dwollaService) {
        this.plaidApi = plaidApi;
        this.bankRepository = bankRepository;
        this.userRepository = userRepository;
        this.dwollaService = dwollaService;
    }

    @Transactional(readOnly = true)
    public String createLinkToken(User user) throws Exception {
        LinkTokenCreateRequestUser plaidUser = new LinkTokenCreateRequestUser()
                .clientUserId(user.getUserId());

        LinkTokenCreateRequest request = new LinkTokenCreateRequest()
                .user(plaidUser)
                .clientName("Stoneridge")
                .products(Arrays.asList(Products.AUTH, Products.TRANSACTIONS))
                .countryCodes(Arrays.asList(CountryCode.US))
                .language("en");

        Response<LinkTokenCreateResponse> response = plaidApi.linkTokenCreate(request).execute();

        if (!response.isSuccessful() || response.body() == null) {
            String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
            log.error("Plaid link token creation failed: {}", errorMsg);
            throw new BadRequestException("Plaid link token creation failed.");
        }

        return response.body().getLinkToken();
    }

    @Transactional
    public void exchangePublicToken(String userId, String publicToken) throws Exception {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for ID: " + userId));

        ItemPublicTokenExchangeRequest exchangeRequest = new ItemPublicTokenExchangeRequest()
                .publicToken(publicToken);
        Response<ItemPublicTokenExchangeResponse> exchangeResponse = plaidApi.itemPublicTokenExchange(exchangeRequest).execute();

        if (!exchangeResponse.isSuccessful() || exchangeResponse.body() == null) {
            String errorMsg = exchangeResponse.errorBody() != null ? exchangeResponse.errorBody().string() : "Unknown error";
            log.error("Plaid public token exchange failed: {}", errorMsg);
            throw new BadRequestException("Plaid public token exchange failed.");
        }

        String accessToken = exchangeResponse.body().getAccessToken();
        String itemId = exchangeResponse.body().getItemId();

        AccountsGetRequest accountsRequest = new AccountsGetRequest()
                .accessToken(accessToken);
        Response<AccountsGetResponse> accountsResponse = plaidApi.accountsGet(accountsRequest).execute();

        if (!accountsResponse.isSuccessful() || accountsResponse.body() == null || 
            accountsResponse.body().getAccounts() == null || accountsResponse.body().getAccounts().isEmpty()) {
            String errorMsg = accountsResponse.errorBody() != null ? accountsResponse.errorBody().string() : "Unknown error or no accounts found";
            log.error("Plaid accounts fetch failed: {}", errorMsg);
            throw new BadRequestException("Plaid accounts fetch failed.");
        }

        AccountBase accountData = accountsResponse.body().getAccounts().get(0);

        ProcessorTokenCreateRequest processorTokenRequest = new ProcessorTokenCreateRequest()
                .accessToken(accessToken)
                .accountId(accountData.getAccountId())
                .processor(ProcessorTokenCreateRequest.ProcessorEnum.DWOLLA);
        Response<ProcessorTokenCreateResponse> processorTokenResponse = plaidApi.processorTokenCreate(processorTokenRequest).execute();

        if (!processorTokenResponse.isSuccessful() || processorTokenResponse.body() == null) {
            String errorMsg = processorTokenResponse.errorBody() != null ? processorTokenResponse.errorBody().string() : "Unknown error";
            log.error("Plaid processor token creation failed: {}", errorMsg);
            throw new BadRequestException("Plaid processor token creation failed.");
        }

        String processorToken = processorTokenResponse.body().getProcessorToken();

        String dwollaCustomerId = user.getDwollaCustomerId();
        String fundingSourceUrl = dwollaService.addFundingSource(dwollaCustomerId, processorToken, accountData.getName());

        String rawAccountId = accountData.getAccountId();
        Bank newBank = Bank.builder()
                .user(user)
                .bankId(itemId)
                .accountId(EncryptionUtil.encrypt(rawAccountId))
                .accountIdHash(EncryptionUtil.hash(rawAccountId))
                .accessToken(EncryptionUtil.encrypt(accessToken))
                .fundingSourceUrl(fundingSourceUrl)
                .shareableId(EncryptionUtil.hash(rawAccountId))
                .mask(accountData.getMask())
                .name(accountData.getName())
                .currentBalance(accountData.getBalances() != null && accountData.getBalances().getCurrent() != null ? accountData.getBalances().getCurrent() : 0.0)
                .build();

        Bank savedBank = bankRepository.save(newBank);
        
        savedBank.setAppwriteItemId(savedBank.getDatabaseId().toString());
        bankRepository.save(savedBank);
    }

    @Transactional(readOnly = true)
    public List<BankDTO> getBanks(String userId) throws Exception {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for ID: " + userId));

        List<Bank> banks = bankRepository.findByUser(user);
        
        return banks.stream()
                .map(bank -> {
                    try {
                        return convertToDTO(bank, true);
                    } catch (Exception e) {
                        log.error("Error decrypting bank: {}", bank.getDatabaseId(), e);
                        try { return convertToDTO(bank, false); } catch(Exception ex) { return null; }
                    }
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<BankDTO> getBank(Long databaseId) {
        return bankRepository.findById(databaseId)
                .map(bank -> {
                    try {
                        return convertToDTO(bank, true);
                    } catch (Exception e) {
                        log.error("Error decrypting bank: {}", bank.getDatabaseId(), e);
                        try { return convertToDTO(bank, false); } catch(Exception ex) { return null; }
                    }
                });
    }

    @Transactional(readOnly = true)
    public Optional<BankDTO> getBankByAccountId(String accountId, String userId) throws Exception {
        Optional<Bank> bankOptional = bankRepository.findByAccountIdHash(EncryptionUtil.hash(accountId));
        
        if (bankOptional.isPresent()) {
            Bank bank = bankOptional.get();
            if (bank.getUser() == null || !bank.getUser().getUserId().equals(userId)) {
                throw new BadRequestException("Bank does not belong to the user.");
            }
            return Optional.of(convertToDTO(bank, true));
        }
        return Optional.empty();
    }

    private BankDTO convertToDTO(Bank bank, boolean decrypt) throws Exception {
        if (bank == null) return null;
        
        BankDTO.BankDTOBuilder builder = BankDTO.builder()
                .databaseId(bank.getDatabaseId())
                .bankId(bank.getBankId())
                .fundingSourceUrl(bank.getFundingSourceUrl())
                .appwriteItemId(bank.getAppwriteItemId())
                .mask(bank.getMask())
                .name(bank.getName())
                .currentBalance(bank.getCurrentBalance())
                .userId(bank.getUser() != null ? bank.getUser().getUserId() : null);

        if (decrypt) {
            builder.accountId(EncryptionUtil.decrypt(bank.getAccountId()))
                   .shareableId(EncryptionUtil.decrypt(bank.getShareableId()));
        } else {
            builder.accountId(bank.getAccountId())
                   .shareableId(bank.getShareableId());
        }
        
        return builder.build();
    }
}
