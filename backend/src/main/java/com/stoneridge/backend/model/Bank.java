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
    private User user;

    @Column(nullable = false)
    private String bankId;

    @Column(nullable = false)
    private String accountId;

    @Column(unique = true, nullable = false)
    private String accountIdHash;

    @Column(nullable = false)
    private String accessToken;

    private String fundingSourceUrl;

    @Column(nullable = false)
    private String shareableId;
    
    private String appwriteItemId;

    private String mask;
    private String name;
    private double currentBalance;

    public Bank() {}

    public static BankBuilder builder() {
        return new BankBuilder();
    }

    public static class BankBuilder {
        private Bank bank = new Bank();
        public BankBuilder databaseId(Long id) { bank.databaseId = id; return this; }
        public BankBuilder user(User user) { bank.user = user; return this; }
        public BankBuilder bankId(String id) { bank.bankId = id; return this; }
        public BankBuilder accountId(String id) { bank.accountId = id; return this; }
        public BankBuilder accountIdHash(String hash) { bank.accountIdHash = hash; return this; }
        public BankBuilder accessToken(String token) { bank.accessToken = token; return this; }
        public BankBuilder fundingSourceUrl(String url) { bank.fundingSourceUrl = url; return this; }
        public BankBuilder shareableId(String id) { bank.shareableId = id; return this; }
        public BankBuilder appwriteItemId(String id) { bank.appwriteItemId = id; return this; }
        public BankBuilder mask(String mask) { bank.mask = mask; return this; }
        public BankBuilder name(String name) { bank.name = name; return this; }
        public BankBuilder currentBalance(double balance) { bank.currentBalance = balance; return this; }
        public Bank build() { return bank; }
    }

    public Long getDatabaseId() { return databaseId; }
    public void setDatabaseId(Long databaseId) { this.databaseId = databaseId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getBankId() { return bankId; }
    public void setBankId(String bankId) { this.bankId = bankId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getAccountIdHash() { return accountIdHash; }
    public void setAccountIdHash(String accountIdHash) { this.accountIdHash = accountIdHash; }
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
