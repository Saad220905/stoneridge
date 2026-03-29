package com.stoneridge.backend.dto;

import com.stoneridge.backend.model.Transaction;
import java.util.Date;

/**
 * Data Transfer Object for Transaction information.
 */
public class TransactionDTO {
    private Long databaseId;
    private String userId;
    private String name;
    private double amount;
    private String currency;
    private Date date;
    private String category;
    private String type;
    private String status;
    private String paymentChannel;
    private String senderId;
    private String senderBankId;
    private String receiverId;
    private String receiverBankId;
    private String email;
    private String appwriteItemId;

    public TransactionDTO() {}

    public static TransactionDTO fromEntity(Transaction transaction) {
        if (transaction == null) return null;
        TransactionDTO dto = new TransactionDTO();
        dto.setDatabaseId(transaction.getDatabaseId());
        if (transaction.getUser() != null) {
            dto.setUserId(transaction.getUser().getUserId());
        }
        dto.setName(transaction.getName());
        dto.setAmount(transaction.getAmount());
        dto.setCurrency(transaction.getCurrency());
        dto.setDate(transaction.getDate());
        dto.setCategory(transaction.getCategory());
        dto.setType(transaction.getType());
        dto.setStatus(transaction.getStatus());
        dto.setPaymentChannel(transaction.getPaymentChannel());
        dto.setSenderId(transaction.getSenderId());
        dto.setSenderBankId(transaction.getSenderBankId());
        dto.setReceiverId(transaction.getReceiverId());
        dto.setReceiverBankId(transaction.getReceiverBankId());
        dto.setEmail(transaction.getEmail());
        dto.setAppwriteItemId(transaction.getAppwriteItemId());
        return dto;
    }

    // Getters and Setters
    public Long getDatabaseId() { return databaseId; }
    public void setDatabaseId(Long databaseId) { this.databaseId = databaseId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentChannel() { return paymentChannel; }
    public void setPaymentChannel(String paymentChannel) { this.paymentChannel = paymentChannel; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getSenderBankId() { return senderBankId; }
    public void setSenderBankId(String senderBankId) { this.senderBankId = senderBankId; }
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public String getReceiverBankId() { return receiverBankId; }
    public void setReceiverBankId(String receiverBankId) { this.receiverBankId = receiverBankId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAppwriteItemId() { return appwriteItemId; }
    public void setAppwriteItemId(String appwriteItemId) { this.appwriteItemId = appwriteItemId; }
}
