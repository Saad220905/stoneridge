package com.stoneridge.backend.dto;

import com.stoneridge.backend.model.Bank;

/**
 * Data Transfer Object for Bank information.
 * Excludes sensitive fields like accessToken.
 */
public class BankDTO {
    private Long databaseId;
    private String userId;
    private String bankId;
    private String accountId;
    private String fundingSourceUrl;
    private String shareableId;
    private String appwriteItemId;
    private String mask;
    private String name;
    private double currentBalance;

    public BankDTO() {}

    public static BankDTO fromEntity(Bank bank) {
        if (bank == null) return null;
        BankDTO dto = new BankDTO();
        dto.setDatabaseId(bank.getDatabaseId());
        if (bank.getUser() != null) {
            dto.setUserId(bank.getUser().getUserId());
        }
        dto.setBankId(bank.getBankId());
        dto.setAccountId(bank.getAccountId());
        dto.setFundingSourceUrl(bank.getFundingSourceUrl());
        dto.setShareableId(bank.getShareableId());
        dto.setAppwriteItemId(bank.getAppwriteItemId());
        dto.setMask(bank.getMask());
        dto.setName(bank.getName());
        dto.setCurrentBalance(bank.getCurrentBalance());
        return dto;
    }

    // Getters and Setters
    public Long getDatabaseId() { return databaseId; }
    public void setDatabaseId(Long databaseId) { this.databaseId = databaseId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getBankId() { return bankId; }
    public void setBankId(String bankId) { this.bankId = bankId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
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
