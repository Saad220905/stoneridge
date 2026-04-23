package com.stoneridge.backend.dto;

public record TransactionRequest(
    String name,
    Double amount,
    String currency,
    String category,
    String type,
    String status,
    String paymentChannel,
    String senderId,
    String senderBankId,
    String receiverId,
    String receiverBankId,
    String email
) {
    public static TransactionRequestBuilder builder() {
        return new TransactionRequestBuilder();
    }

    public static class TransactionRequestBuilder {
        private String name;
        private Double amount;
        private String currency;
        private String category;
        private String type;
        private String status;
        private String paymentChannel;
        private String senderId;
        private String senderBankId;
        private String receiverId;
        private String receiverBankId;
        private String email;

        public TransactionRequestBuilder name(String name) { this.name = name; return this; }
        public TransactionRequestBuilder amount(Double amount) { this.amount = amount; return this; }
        public TransactionRequestBuilder currency(String currency) { this.currency = currency; return this; }
        public TransactionRequestBuilder category(String category) { this.category = category; return this; }
        public TransactionRequestBuilder type(String type) { this.type = type; return this; }
        public TransactionRequestBuilder status(String status) { this.status = status; return this; }
        public TransactionRequestBuilder paymentChannel(String channel) { this.paymentChannel = channel; return this; }
        public TransactionRequestBuilder senderId(String id) { this.senderId = id; return this; }
        public TransactionRequestBuilder senderBankId(String id) { this.senderBankId = id; return this; }
        public TransactionRequestBuilder receiverId(String id) { this.receiverId = id; return this; }
        public TransactionRequestBuilder receiverBankId(String id) { this.receiverBankId = id; return this; }
        public TransactionRequestBuilder email(String email) { this.email = email; return this; }

        public TransactionRequest build() {
            return new TransactionRequest(name, amount, currency, category, type, status, paymentChannel, senderId, senderBankId, receiverId, receiverBankId, email);
        }
    }
}
