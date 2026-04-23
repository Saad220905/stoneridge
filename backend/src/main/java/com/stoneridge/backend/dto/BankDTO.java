package com.stoneridge.backend.dto;

import com.stoneridge.backend.model.Bank;

public class BankDTO {
    private String id;
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

    public static BankDTOBuilder builder() {
        return new BankDTOBuilder();
    }

    public static class BankDTOBuilder {
        private BankDTO dto = new BankDTO();
        public BankDTOBuilder id(String id) { dto.id = id; return this; }
        public BankDTOBuilder databaseId(Long id) { dto.databaseId = id; return this; }
        public BankDTOBuilder userId(String id) { dto.userId = id; return this; }
        public BankDTOBuilder bankId(String id) { dto.bankId = id; return this; }
        public BankDTOBuilder accountId(String id) { dto.accountId = id; return this; }
        public BankDTOBuilder fundingSourceUrl(String url) { dto.fundingSourceUrl = url; return this; }
        public BankDTOBuilder shareableId(String id) { dto.shareableId = id; return this; }
        public BankDTOBuilder appwriteItemId(String id) { dto.appwriteItemId = id; return this; }
        public BankDTOBuilder mask(String mask) { dto.mask = mask; return this; }
        public BankDTOBuilder name(String name) { dto.name = name; return this; }
        public BankDTOBuilder currentBalance(double balance) { dto.currentBalance = balance; return this; }
        public BankDTO build() { return dto; }
    }

    public static BankDTO fromEntity(Bank bank) {
        if (bank == null) return null;
        return BankDTO.builder()
                .id(bank.getAppwriteItemId())
                .databaseId(bank.getDatabaseId())
                .userId(bank.getUser() != null ? bank.getUser().getUserId() : null)
                .bankId(bank.getBankId())
                .accountId(bank.getAccountId())
                .fundingSourceUrl(bank.getFundingSourceUrl())
                .shareableId(bank.getShareableId())
                .appwriteItemId(bank.getAppwriteItemId())
                .mask(bank.getMask())
                .name(bank.getName())
                .currentBalance(bank.getCurrentBalance())
                .build();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
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
