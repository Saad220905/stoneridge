package com.stoneridge.backend.dto;

import java.util.Date;

public class TransactionDTO {
    private Long databaseId;
    private String appwriteItemId;
    private String name;
    private double amount;
    private String currency;
    private Date date;
    private String category;
    private String type;
    private String status;
    private String paymentChannel;
    private String senderId;
    private String receiverId;
    private String senderBankId;
    private String receiverBankId;
    private String email;
    private String userId;

    public TransactionDTO() {}

    public static TransactionDTOBuilder builder() {
        return new TransactionDTOBuilder();
    }

    public static class TransactionDTOBuilder {
        private TransactionDTO dto = new TransactionDTO();
        public TransactionDTOBuilder databaseId(Long id) { dto.databaseId = id; return this; }
        public TransactionDTOBuilder appwriteItemId(String id) { dto.appwriteItemId = id; return this; }
        public TransactionDTOBuilder name(String name) { dto.name = name; return this; }
        public TransactionDTOBuilder amount(double amount) { dto.amount = amount; return this; }
        public TransactionDTOBuilder currency(String currency) { dto.currency = currency; return this; }
        public TransactionDTOBuilder date(Date date) { dto.date = date; return this; }
        public TransactionDTOBuilder category(String category) { dto.category = category; return this; }
        public TransactionDTOBuilder type(String type) { dto.type = type; return this; }
        public TransactionDTOBuilder status(String status) { dto.status = status; return this; }
        public TransactionDTOBuilder paymentChannel(String channel) { dto.paymentChannel = channel; return this; }
        public TransactionDTOBuilder senderId(String id) { dto.senderId = id; return this; }
        public TransactionDTOBuilder receiverId(String id) { dto.receiverId = id; return this; }
        public TransactionDTOBuilder senderBankId(String id) { dto.senderBankId = id; return this; }
        public TransactionDTOBuilder receiverBankId(String id) { dto.receiverBankId = id; return this; }
        public TransactionDTOBuilder email(String email) { dto.email = email; return this; }
        public TransactionDTOBuilder userId(String id) { dto.userId = id; return this; }
        public TransactionDTO build() { return dto; }
    }

    public Long getDatabaseId() { return databaseId; }
    public void setDatabaseId(Long databaseId) { this.databaseId = databaseId; }
    public String getAppwriteItemId() { return appwriteItemId; }
    public void setAppwriteItemId(String appwriteItemId) { this.appwriteItemId = appwriteItemId; }
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
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public String getSenderBankId() { return senderBankId; }
    public void setSenderBankId(String senderBankId) { this.senderBankId = senderBankId; }
    public String getReceiverBankId() { return receiverBankId; }
    public void setReceiverBankId(String receiverBankId) { this.receiverBankId = receiverBankId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
