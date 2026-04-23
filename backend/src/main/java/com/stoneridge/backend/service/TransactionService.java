package com.stoneridge.backend.service;

import com.stoneridge.backend.dto.TransactionDTO;
import com.stoneridge.backend.dto.TransactionRequest;
import com.stoneridge.backend.exception.BadRequestException;
import com.stoneridge.backend.exception.ResourceNotFoundException;
import com.stoneridge.backend.model.Bank;
import com.stoneridge.backend.model.Transaction;
import com.stoneridge.backend.model.User;
import com.stoneridge.backend.repository.BankRepository;
import com.stoneridge.backend.repository.TransactionRepository;
import com.stoneridge.backend.repository.UserRepository;
import com.stoneridge.backend.util.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BankRepository bankRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, BankRepository bankRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.bankRepository = bankRepository;
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactions(String userId, String bankId, int page, int pageSize) throws Exception {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for ID: " + userId));

        Pageable pageable = PageRequest.of(page - 1, pageSize);

        if (bankId != null && !bankId.isBlank()) {
            Long bankDbId;
            try {
                bankDbId = Long.valueOf(bankId);
            } catch (NumberFormatException e) {
                Optional<Bank> bankByPlaid = bankRepository.findByAppwriteItemId(bankId);
                if (bankByPlaid.isPresent()) {
                    bankDbId = bankByPlaid.get().getDatabaseId();
                } else {
                    throw new BadRequestException("Invalid bank ID format: " + bankId);
                }
            }

            Optional<Bank> bankOptional = bankRepository.findById(bankDbId);
            if (bankOptional.isEmpty() || bankOptional.get().getUser() == null || !bankOptional.get().getUser().getUserId().equals(user.getUserId())) {
                throw new ResourceNotFoundException("Bank not found or does not belong to user.");
            }
            
            List<Transaction> bankTransactions = transactionRepository.findByUserAndBank_DatabaseIdOrderByDateDesc(user, bankDbId);
            
            return bankTransactions.stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .map(t -> {
                        try {
                            return convertToDTO(t, true);
                        } catch (Exception e) {
                            log.error("Error decrypting transaction: {}", t.getDatabaseId(), e);
                            try { return convertToDTO(t, false); } catch(Exception ex) { return null; }
                        }
                    })
                    .collect(Collectors.toList());

        } else {
            Page<Transaction> transactionPage = transactionRepository.findByUserOrderByDateDesc(user, pageable);
            return transactionPage.getContent().stream()
                    .map(t -> {
                        try {
                            return convertToDTO(t, true);
                        } catch (Exception e) {
                            log.error("Error decrypting transaction: {}", t.getDatabaseId(), e);
                            try { return convertToDTO(t, false); } catch(Exception ex) { return null; }
                        }
                    })
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public TransactionDTO createTransaction(String userId, TransactionRequest transactionData) throws Exception {
        if (transactionData == null) throw new BadRequestException("Transaction data is required.");
        
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for ID: " + userId));

        Transaction.TransactionBuilder transactionBuilder = Transaction.builder()
                .user(user)
                .name(transactionData.name() != null ? transactionData.name() : "Unknown")
                .amount(transactionData.amount() != null ? transactionData.amount() : 0.0)
                .currency(transactionData.currency() != null ? transactionData.currency() : "USD")
                .date(new Date())
                .category(transactionData.category() != null ? transactionData.category() : "General")
                .type(transactionData.type() != null ? transactionData.type() : "debit")
                .status(transactionData.status() != null ? transactionData.status() : "Success")
                .paymentChannel(transactionData.paymentChannel() != null ? transactionData.paymentChannel() : "online")
                .senderId(EncryptionUtil.encrypt(transactionData.senderId() != null ? transactionData.senderId() : ""))
                .receiverId(EncryptionUtil.encrypt(transactionData.receiverId() != null ? transactionData.receiverId() : ""))
                .email(EncryptionUtil.encrypt(transactionData.email() != null ? transactionData.email() : ""));

        if (transactionData.senderBankId() != null && !transactionData.senderBankId().isBlank()) {
            try {
                Long bankDbId = Long.valueOf(transactionData.senderBankId());
                bankRepository.findById(bankDbId).ifPresent(transactionBuilder::bank);
            } catch (NumberFormatException e) {
                bankRepository.findByAppwriteItemId(transactionData.senderBankId()).ifPresent(transactionBuilder::bank);
            }
            transactionBuilder.senderBankId(EncryptionUtil.encrypt(transactionData.senderBankId()));
        }
        
        if (transactionData.receiverBankId() != null && !transactionData.receiverBankId().isBlank()) {
            transactionBuilder.receiverBankId(EncryptionUtil.encrypt(transactionData.receiverBankId()));
        }

        Transaction savedTransaction = transactionRepository.save(transactionBuilder.build());
        
        savedTransaction.setAppwriteItemId(savedTransaction.getDatabaseId().toString());
        Transaction updated = transactionRepository.save(savedTransaction);
        
        return convertToDTO(updated, true);
    }

    private TransactionDTO convertToDTO(Transaction t, boolean decrypt) throws Exception {
        if (t == null) return null;
        
        TransactionDTO.TransactionDTOBuilder builder = TransactionDTO.builder()
                .databaseId(t.getDatabaseId())
                .appwriteItemId(t.getAppwriteItemId())
                .name(t.getName())
                .amount(t.getAmount())
                .currency(t.getCurrency())
                .date(t.getDate())
                .category(t.getCategory())
                .type(t.getType())
                .status(t.getStatus())
                .paymentChannel(t.getPaymentChannel())
                .userId(t.getUser() != null ? t.getUser().getUserId() : null);

        if (decrypt) {
            builder.senderId(EncryptionUtil.decrypt(t.getSenderId()))
                   .receiverId(EncryptionUtil.decrypt(t.getReceiverId()))
                   .senderBankId(EncryptionUtil.decrypt(t.getSenderBankId()))
                   .receiverBankId(EncryptionUtil.decrypt(t.getReceiverBankId()))
                   .email(EncryptionUtil.decrypt(t.getEmail()));
        } else {
            builder.senderId(t.getSenderId())
                   .receiverId(t.getReceiverId())
                   .senderBankId(t.getSenderBankId())
                   .receiverBankId(t.getReceiverBankId())
                   .email(t.getEmail());
        }
        
        return builder.build();
    }
}
