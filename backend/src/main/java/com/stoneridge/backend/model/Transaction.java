package com.stoneridge.backend.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long databaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_database_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_database_id")
    private Bank bank;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
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

    public Transaction() {}

    public static TransactionBuilder builder() {
        return new TransactionBuilder();
    }

    public static class TransactionBuilder {
        private Transaction transaction = new Transaction();
        public TransactionBuilder databaseId(Long id) { transaction.databaseId = id; return this; }
        public TransactionBuilder user(User user) { transaction.user = user; return this; }
        public TransactionBuilder bank(Bank bank) { transaction.bank = bank; return this; }
        public TransactionBuilder name(String name) { transaction.name = name; return this; }
        public TransactionBuilder amount(double amount) { transaction.amount = amount; return this; }
        public TransactionBuilder currency(String currency) { transaction.currency = currency; return this; }
        public TransactionBuilder date(Date date) { transaction.date = date; return this; }
        public TransactionBuilder category(String category) { transaction.category = category; return this; }
        public TransactionBuilder type(String type) { transaction.type = type; return this; }
        public TransactionBuilder status(String status) { transaction.status = status; return this; }
        public TransactionBuilder paymentChannel(String channel) { transaction.paymentChannel = channel; return this; }
        public TransactionBuilder senderId(String id) { transaction.senderId = id; return this; }
        public TransactionBuilder senderBankId(String id) { transaction.senderBankId = id; return this; }
        public TransactionBuilder receiverId(String id) { transaction.receiverId = id; return this; }
        public TransactionBuilder receiverBankId(String id) { transaction.receiverBankId = id; return this; }
        public TransactionBuilder email(String email) { transaction.email = email; return this; }
        public TransactionBuilder appwriteItemId(String id) { transaction.appwriteItemId = id; return this; }
        public Transaction build() { return transaction; }
    }

    public Long getDatabaseId() { return databaseId; }
    public void setDatabaseId(Long databaseId) { this.databaseId = databaseId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Bank getBank() { return bank; }
    public void setBank(Bank bank) { this.bank = bank; }
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
