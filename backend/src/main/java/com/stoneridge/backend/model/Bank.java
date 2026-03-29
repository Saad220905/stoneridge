package com.stoneridge.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "banks")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long databaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_database_id", nullable = false)
    private User user; // Reference to the User entity

    @Column(nullable = false)
    private String bankId; // Plaid item ID

    @Column(nullable = false)
    private String accountId; // Encrypted Plaid account ID

    @Column(nullable = false)
    private String accessToken; // Encrypted Plaid access token

    private String fundingSourceUrl;

    @Column(nullable = false)
    private String shareableId; // Encrypted account ID
    
    private String appwriteItemId; // For compatibility with frontend components expecting this field

    private String mask;
    private String name;
    private double currentBalance;

    // No-argument constructor
    public Bank() {
    }

    // All-argument constructor
    public Bank(Long databaseId, User user, String bankId, String accountId, String accessToken,
                String fundingSourceUrl, String shareableId, String appwriteItemId,
                String mask, String name, double currentBalance) {
        this.databaseId = databaseId;
        this.user = user;
        this.bankId = bankId;
        this.accountId = accountId;
        this.accessToken = accessToken;
        this.fundingSourceUrl = fundingSourceUrl;
        this.shareableId = shareableId;
        this.appwriteItemId = appwriteItemId;
        this.mask = mask;
        this.name = name;
        this.currentBalance = currentBalance;
    }

    // Manual getters and setters
    public Long getDatabaseId() { return databaseId; }
    public void setDatabaseId(Long databaseId) { this.databaseId = databaseId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getBankId() { return bankId; }
    public void setBankId(String bankId) { this.bankId = bankId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getFundingSourceUrl() { return fundingSourceUrl; }
    public void setFundingSourceUrl(String fundingSourceUrl) { this.fundingSourceUrl = fundingSourceUrl; }
    public String getShareableId() { return shareableId; }
    public void setShareableId(String shareableId) { this.shareableId = shareableId; }
    public String getAppwriteItemId() { return appwriteItemId; }
    public void setAppwriteItemId(String appwriteItemId) { this.appwriteItemId = appwriteItemId; }
    public String getMask() { return mask; }
    public void setMask(String mask) { this.mask = mask; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(double currentBalance) { this.currentBalance = currentBalance; }
}
