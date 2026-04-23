package com.stoneridge.backend.dto;

public record DwollaTransferRequest(
    String sourceFundingSourceUrl,
    String destinationFundingSourceUrl,
    Double amount,
    String senderId,
    String senderBankId,
    String receiverId,
    String receiverBankId,
    String email
) {
    public static DwollaTransferRequestBuilder builder() {
        return new DwollaTransferRequestBuilder();
    }

    public static class DwollaTransferRequestBuilder {
        private String sourceFundingSourceUrl;
        private String destinationFundingSourceUrl;
        private Double amount;
        private String senderId;
        private String senderBankId;
        private String receiverId;
        private String receiverBankId;
        private String email;

        public DwollaTransferRequestBuilder sourceFundingSourceUrl(String url) { this.sourceFundingSourceUrl = url; return this; }
        public DwollaTransferRequestBuilder destinationFundingSourceUrl(String url) { this.destinationFundingSourceUrl = url; return this; }
        public DwollaTransferRequestBuilder amount(Double amount) { this.amount = amount; return this; }
        public DwollaTransferRequestBuilder senderId(String id) { this.senderId = id; return this; }
        public DwollaTransferRequestBuilder senderBankId(String id) { this.senderBankId = id; return this; }
        public DwollaTransferRequestBuilder receiverId(String id) { this.receiverId = id; return this; }
        public DwollaTransferRequestBuilder receiverBankId(String id) { this.receiverBankId = id; return this; }
        public DwollaTransferRequestBuilder email(String email) { this.email = email; return this; }

        public DwollaTransferRequest build() {
            return new DwollaTransferRequest(sourceFundingSourceUrl, destinationFundingSourceUrl, amount, senderId, senderBankId, receiverId, receiverBankId, email);
        }
    }
}
