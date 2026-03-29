package com.stoneridge.backend.dto;

public class TransactionRequest {
    private String name;
    private Double amount;
    private String currency;
    private String category;
    private String type;
    private String status;
    private String paymentChannel;
    private String senderId;
    private String receiverId;
    private String email;
    private String senderBankId;
    private String receiverBankId;

    public TransactionRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

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

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenderBankId() { return senderBankId; }
    public void setSenderBankId(String senderBankId) { this.senderBankId = senderBankId; }

    public String getReceiverBankId() { return receiverBankId; }
    public void setReceiverBankId(String receiverBankId) { this.receiverBankId = receiverBankId; }
}
