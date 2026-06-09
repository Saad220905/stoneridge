package com.stoneridge.backend.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bill_split_participants")
public class BillSplitParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long databaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_split_database_id", nullable = false)
    private BillSplit billSplit;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String emailHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_database_id")
    private User user;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String status; // PENDING, PAID, DECLINED

    @Temporal(TemporalType.TIMESTAMP)
    private Date paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_database_id")
    private Transaction transaction;

    public BillSplitParticipant() {}

    public static BillSplitParticipantBuilder builder() {
        return new BillSplitParticipantBuilder();
    }

    public static class BillSplitParticipantBuilder {
        private BillSplitParticipant participant = new BillSplitParticipant();
        public BillSplitParticipantBuilder databaseId(Long id) { participant.databaseId = id; return this; }
        public BillSplitParticipantBuilder billSplit(BillSplit billSplit) { participant.billSplit = billSplit; return this; }
        public BillSplitParticipantBuilder email(String email) { participant.email = email; return this; }
        public BillSplitParticipantBuilder emailHash(String emailHash) { participant.emailHash = emailHash; return this; }
        public BillSplitParticipantBuilder user(User user) { participant.user = user; return this; }
        public BillSplitParticipantBuilder amount(double amount) { participant.amount = amount; return this; }
        public BillSplitParticipantBuilder status(String status) { participant.status = status; return this; }
        public BillSplitParticipantBuilder paidAt(Date paidAt) { participant.paidAt = paidAt; return this; }
        public BillSplitParticipantBuilder transaction(Transaction transaction) { participant.transaction = transaction; return this; }
        public BillSplitParticipant build() { return participant; }
    }

    public Long getDatabaseId() { return databaseId; }
    public void setDatabaseId(Long databaseId) { this.databaseId = databaseId; }
    public BillSplit getBillSplit() { return billSplit; }
    public void setBillSplit(BillSplit billSplit) { this.billSplit = billSplit; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEmailHash() { return emailHash; }
    public void setEmailHash(String emailHash) { this.emailHash = emailHash; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getPaidAt() { return paidAt; }
    public void setPaidAt(Date paidAt) { this.paidAt = paidAt; }
    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
}
