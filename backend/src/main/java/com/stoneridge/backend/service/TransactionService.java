package com.stoneridge.backend.service;

import com.stoneridge.backend.dto.TransactionDTO;
import com.stoneridge.backend.dto.TransactionRequest;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing Transactions.
 * Handles transaction creation, retrieval, and decryption.
 */
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

    public List<TransactionDTO> getTransactions(String userId, String bankId, int page, int pageSize) throws Exception {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for ID: " + userId));

        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Page<Transaction> transactionPage;

        if (bankId != null && !bankId.isEmpty()) {
            Optional<Bank> bankOptional = bankRepository.findById(Long.valueOf(bankId));
            if (bankOptional.isEmpty() || bankOptional.get().getUser() == null || !bankOptional.get().getUser().getUserId().equals(user.getUserId())) {
                throw new Exception("Bank not found or does not belong to user.");
            }
            List<Transaction> bankTransactions = transactionRepository.findByUserAndBankDatabaseIdOrderByDateDesc(user, bankOptional.get().getDatabaseId());
            if (bankTransactions == null) return java.util.Collections.emptyList();

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), bankTransactions.size());
            if (start > bankTransactions.size()) return java.util.Collections.emptyList();
            
            return bankTransactions.subList(start, end).stream()
                    .map(t -> convertToDTO(decryptTransactionFields(t)))
                    .collect(Collectors.toList());

        } else {
            transactionPage = transactionRepository.findByUserOrderByDateDesc(user, pageable);
        }

        if (transactionPage == null) return java.util.Collections.emptyList();

        return transactionPage.getContent().stream()
                .map(t -> convertToDTO(decryptTransactionFields(t)))
                .collect(Collectors.toList());
    }

    public TransactionDTO createTransaction(String userId, TransactionRequest transactionData) throws Exception {
        if (transactionData == null) throw new Exception("Transaction data is required.");
        
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for ID: " + userId));

        Transaction newTransaction = new Transaction();
        newTransaction.setUser(user);
        newTransaction.setName(transactionData.getName() != null ? transactionData.getName() : "Unknown");
        newTransaction.setAmount(transactionData.getAmount() != null ? transactionData.getAmount() : 0.0);
        newTransaction.setCurrency(transactionData.getCurrency() != null ? transactionData.getCurrency() : "USD");
        newTransaction.setDate(new Date());
        newTransaction.setCategory(transactionData.getCategory() != null ? transactionData.getCategory() : "General");
        newTransaction.setType(transactionData.getType() != null ? transactionData.getType() : "debit");
        newTransaction.setStatus(transactionData.getStatus() != null ? transactionData.getStatus() : "Success");
        newTransaction.setPaymentChannel(transactionData.getPaymentChannel() != null ? transactionData.getPaymentChannel() : "online");

        newTransaction.setSenderId(EncryptionUtil.encrypt(transactionData.getSenderId() != null ? transactionData.getSenderId() : ""));
        newTransaction.setReceiverId(EncryptionUtil.encrypt(transactionData.getReceiverId() != null ? transactionData.getReceiverId() : ""));
        newTransaction.setEmail(EncryptionUtil.encrypt(transactionData.getEmail() != null ? transactionData.getEmail() : ""));

        String senderBankIdStr = transactionData.getSenderBankId();
        if (senderBankIdStr != null && !senderBankIdStr.isEmpty()) {
            Optional<Bank> senderBank = bankRepository.findById(Long.valueOf(senderBankIdStr));
            senderBank.ifPresent(newTransaction::setBank);
            newTransaction.setSenderBankId(EncryptionUtil.encrypt(senderBankIdStr));
        }
        
        String receiverBankIdStr = transactionData.getReceiverBankId();
        if (receiverBankIdStr != null && !receiverBankIdStr.isEmpty()) {
            newTransaction.setReceiverBankId(EncryptionUtil.encrypt(receiverBankIdStr));
        }

        Transaction savedTransaction = transactionRepository.save(newTransaction);
        savedTransaction.setAppwriteItemId(savedTransaction.getDatabaseId().toString());
        return convertToDTO(decryptTransactionFields(transactionRepository.save(savedTransaction)));
    }

    private Transaction decryptTransactionFields(Transaction transaction) {
        if (transaction == null) return null;
        try {
            if (transaction.getSenderId() != null) {
                transaction.setSenderId(EncryptionUtil.decrypt(transaction.getSenderId()));
            }
            if (transaction.getSenderBankId() != null) {
                transaction.setSenderBankId(EncryptionUtil.decrypt(transaction.getSenderBankId()));
            }
            if (transaction.getReceiverId() != null) {
                transaction.setReceiverId(EncryptionUtil.decrypt(transaction.getReceiverId()));
            }
            if (transaction.getReceiverBankId() != null) {
                transaction.setReceiverBankId(EncryptionUtil.decrypt(transaction.getReceiverBankId()));
            }
            if (transaction.getEmail() != null) {
                transaction.setEmail(EncryptionUtil.decrypt(transaction.getEmail()));
            }
            
            if (transaction.getDatabaseId() != null && (transaction.getAppwriteItemId() == null || transaction.getAppwriteItemId().isEmpty())) {
                transaction.setAppwriteItemId(transaction.getDatabaseId().toString());
            }
        } catch (Exception e) {
            log.error("Failed to decrypt transaction data for ID {}: {}", 
                transaction.getDatabaseId() != null ? transaction.getDatabaseId() : "unknown", e.getMessage());
        }
        return transaction;
    }

    private TransactionDTO convertToDTO(Transaction t) {
        if (t == null) return null;
        TransactionDTO dto = new TransactionDTO();
        dto.setDatabaseId(t.getDatabaseId());
        dto.setAppwriteItemId(t.getAppwriteItemId());
        dto.setName(t.getName());
        dto.setAmount(t.getAmount());
        dto.setCurrency(t.getCurrency());
        dto.setDate(t.getDate());
        dto.setCategory(t.getCategory());
        dto.setType(t.getType());
        dto.setStatus(t.getStatus());
        dto.setPaymentChannel(t.getPaymentChannel());
        dto.setSenderId(t.getSenderId());
        dto.setReceiverId(t.getReceiverId());
        dto.setSenderBankId(t.getSenderBankId());
        dto.setReceiverBankId(t.getReceiverBankId());
        dto.setEmail(t.getEmail());
        if (t.getUser() != null) {
            dto.setUserId(t.getUser().getUserId());
        }
        return dto;
    }
}
