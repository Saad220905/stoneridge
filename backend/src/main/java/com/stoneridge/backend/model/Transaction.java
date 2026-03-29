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
    private User user; // The user who initiated/owns this transaction

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_database_id") // Optional: can be null for peer-to-peer transfers not directly tied to one's own bank account
    private Bank bank; // The bank account associated with this transaction (if applicable)

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private Date date;

    private String category;
    private String type; // e.g., 'credit' or 'debit'
    private String status; // e.g., 'pending', 'posted'
    private String paymentChannel;

    // Fields for transfers - these are now stored as encrypted strings
    private String senderId; // The userId of the sender (encrypted)
    private String senderBankId; // The bankId of the sender's bank (encrypted)
    private String receiverId; // The userId of the receiver (encrypted)
    private String receiverBankId; // The bankId of the receiver's bank (encrypted)
    private String email; // Receiver's email for transfers (encrypted)
    
    private String appwriteItemId; // For compatibility with frontend components

    // No-argument constructor
    public Transaction() {
    }

    // All-argument constructor
    public Transaction(Long databaseId, User user, Bank bank, String name, double amount, String currency, Date date,
                       String category, String type, String status, String paymentChannel, String senderId,
                       String senderBankId, String receiverId, String receiverBankId, String email, String appwriteItemId) {
        this.databaseId = databaseId;
        this.user = user;
        this.bank = bank;
        this.name = name;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.category = category;
        this.type = type;
        this.status = status;
        this.paymentChannel = paymentChannel;
        this.senderId = senderId;
        this.senderBankId = senderBankId;
        this.receiverId = receiverId;
        this.receiverBankId = receiverBankId;
        this.email = email;
        this.appwriteItemId = appwriteItemId;
    }

    // Manual getters and setters
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
