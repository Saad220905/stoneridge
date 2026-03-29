package com.stoneridge.backend.dto;

public class DwollaTransferRequest {
    private String sourceFundingSourceUrl;
    private String destinationFundingSourceUrl;
    private String amount;

    public DwollaTransferRequest() {}

    public String getSourceFundingSourceUrl() { return sourceFundingSourceUrl; }
    public void setSourceFundingSourceUrl(String sourceFundingSourceUrl) { this.sourceFundingSourceUrl = sourceFundingSourceUrl; }

    public String getDestinationFundingSourceUrl() { return destinationFundingSourceUrl; }
    public void setDestinationFundingSourceUrl(String destinationFundingSourceUrl) { this.destinationFundingSourceUrl = destinationFundingSourceUrl; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
}
