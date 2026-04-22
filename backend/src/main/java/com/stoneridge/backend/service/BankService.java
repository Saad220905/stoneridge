package com.stoneridge.backend.service;

import com.plaid.client.model.*;
import com.plaid.client.request.PlaidApi;
import com.stoneridge.backend.dto.BankDTO;
import com.stoneridge.backend.exception.ResourceNotFoundException;
import com.stoneridge.backend.model.Bank;
import com.stoneridge.backend.model.User;
import com.stoneridge.backend.repository.BankRepository;
import com.stoneridge.backend.repository.UserRepository;
import com.stoneridge.backend.util.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing Bank accounts and Plaid integrations.
 * Handles link token creation, public token exchange, and bank data retrieval.
 */
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
            throw new Exception("Plaid link token creation failed: " + (response.errorBody() != null ? response.errorBody().string() : "Unknown error"));
        }

        return response.body().getLinkToken();
    }

    public void exchangePublicToken(String userId, String publicToken) throws Exception {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for ID: " + userId));

        // 1. Exchange public token for access token
        ItemPublicTokenExchangeRequest exchangeRequest = new ItemPublicTokenExchangeRequest()
                .publicToken(publicToken);
        Response<ItemPublicTokenExchangeResponse> exchangeResponse = plaidApi.itemPublicTokenExchange(exchangeRequest).execute();

        if (!exchangeResponse.isSuccessful() || exchangeResponse.body() == null) {
            throw new Exception("Plaid public token exchange failed: " + (exchangeResponse.errorBody() != null ? exchangeResponse.errorBody().string() : "Unknown error"));
        }

        String accessToken = exchangeResponse.body().getAccessToken();
        String itemId = exchangeResponse.body().getItemId();

        // 2. Get account data from Plaid
        AccountsGetRequest accountsRequest = new AccountsGetRequest()
                .accessToken(accessToken);
        Response<AccountsGetResponse> accountsResponse = plaidApi.accountsGet(accountsRequest).execute();

        if (!accountsResponse.isSuccessful() || accountsResponse.body() == null || 
            accountsResponse.body().getAccounts() == null || accountsResponse.body().getAccounts().isEmpty()) {
            throw new Exception("Plaid accounts fetch failed: " + (accountsResponse.errorBody() != null ? accountsResponse.errorBody().string() : "Unknown error or no accounts found"));
        }

        AccountBase accountData = accountsResponse.body().getAccounts().get(0);

        // 3. Create processor token for Dwolla
        ProcessorTokenCreateRequest processorTokenRequest = new ProcessorTokenCreateRequest()
                .accessToken(accessToken)
                .accountId(accountData.getAccountId())
                .processor(ProcessorTokenCreateRequest.ProcessorEnum.DWOLLA);
        Response<ProcessorTokenCreateResponse> processorTokenResponse = plaidApi.processorTokenCreate(processorTokenRequest).execute();

        if (!processorTokenResponse.isSuccessful() || processorTokenResponse.body() == null) {
            throw new Exception("Plaid processor token creation failed: " + (processorTokenResponse.errorBody() != null ? processorTokenResponse.errorBody().string() : "Unknown error"));
        }

        String processorToken = processorTokenResponse.body().getProcessorToken();

        // 4. Create Dwolla funding source
        String dwollaCustomerId = user.getDwollaCustomerId();
        String fundingSourceUrl = dwollaService.addFundingSource(dwollaCustomerId, processorToken, accountData.getName());

        // 5. Save bank details to database
        Bank newBank = new Bank();
        newBank.setUser(user);
        newBank.setBankId(itemId);
        String rawAccountId = accountData.getAccountId();
        newBank.setAccountId(EncryptionUtil.encrypt(rawAccountId));
        newBank.setAccountIdHash(EncryptionUtil.hash(rawAccountId));
        newBank.setAccessToken(EncryptionUtil.encrypt(accessToken));
        newBank.setFundingSourceUrl(fundingSourceUrl);
        
        // Use deterministic hash for shareableId to allow searching
        newBank.setShareableId(EncryptionUtil.hash(rawAccountId));
        
        newBank.setMask(accountData.getMask());
        newBank.setName(accountData.getName());
        
        double balance = 0.0;
        if (accountData.getBalances() != null && accountData.getBalances().getCurrent() != null) {
            balance = accountData.getBalances().getCurrent();
        }
        newBank.setCurrentBalance(balance);

        Bank savedBank = bankRepository.save(newBank);
        
        if (savedBank.getDatabaseId() != null) {
            savedBank.setAppwriteItemId(savedBank.getDatabaseId().toString());
            bankRepository.save(savedBank);
        }
    }

    public List<BankDTO> getBanks(String userId) throws Exception {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for ID: " + userId));

        List<Bank> banks = bankRepository.findByUser(user);
        if (banks == null) return java.util.Collections.emptyList();

        return banks.stream()
                .map(bank -> convertToDTO(decryptBankFields(bank)))
                .collect(Collectors.toList());
    }

    public Optional<BankDTO> getBank(Long databaseId) {
        return bankRepository.findById(databaseId)
                .map(bank -> convertToDTO(decryptBankFields(bank)));
    }

    public Optional<BankDTO> getBankByAccountId(String accountId, String userId) throws Exception {
        Optional<Bank> bankOptional = bankRepository.findByAccountIdHash(EncryptionUtil.hash(accountId));
        
        if (bankOptional.isPresent()) {
            Bank bank = bankOptional.get();
            if (bank.getUser() == null || !bank.getUser().getUserId().equals(userId)) {
                throw new Exception("Bank does not belong to the user or user not found for bank.");
            }
            return Optional.of(convertToDTO(decryptBankFields(bank)));
        }
        return Optional.empty();
    }

    private Bank decryptBankFields(Bank bank) {
        if (bank == null) return null;
        try {
            if (bank.getAccountId() != null) {
                bank.setAccountId(EncryptionUtil.decrypt(bank.getAccountId()));
            }
            if (bank.getAccessToken() != null) {
                bank.setAccessToken(EncryptionUtil.decrypt(bank.getAccessToken()));
            }
            if (bank.getShareableId() != null) {
                bank.setShareableId(EncryptionUtil.decrypt(bank.getShareableId()));
            }
            
            if (bank.getDatabaseId() != null && (bank.getAppwriteItemId() == null || bank.getAppwriteItemId().isEmpty())) {
                bank.setAppwriteItemId(bank.getDatabaseId().toString());
            }
        } catch (Exception e) {
            log.error("Failed to decrypt bank data for bank ID {}: {}", 
                bank.getDatabaseId() != null ? bank.getDatabaseId() : "unknown", e.getMessage());
        }
        return bank;
    }

    private BankDTO convertToDTO(Bank bank) {
        if (bank == null) return null;
        BankDTO dto = new BankDTO();
        dto.setDatabaseId(bank.getDatabaseId());
        dto.setAccountId(bank.getAccountId());
        dto.setBankId(bank.getBankId());
        dto.setFundingSourceUrl(bank.getFundingSourceUrl());
        dto.setShareableId(bank.getShareableId());
        dto.setAppwriteItemId(bank.getAppwriteItemId());
        dto.setMask(bank.getMask());
        dto.setName(bank.getName());
        dto.setCurrentBalance(bank.getCurrentBalance());
        if (bank.getUser() != null) {
            dto.setUserId(bank.getUser().getUserId());
        }
        return dto;
    }
}
