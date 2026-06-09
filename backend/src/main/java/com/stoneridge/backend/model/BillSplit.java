package com.stoneridge.backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "bill_splits")
public class BillSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long databaseId;

    @Column(nullable = false)
    private String billName;

    @Column(nullable = false)
    private double totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_database_id", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_bank_database_id", nullable = false)
    private Bank creatorBank;

    @Column(nullable = false)
    private String status; // PENDING, COMPLETED, CANCELLED

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt;

    @OneToMany(mappedBy = "billSplit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BillSplitParticipant> participants = new ArrayList<>();

    public BillSplit() {}

    public static BillSplitBuilder builder() {
        return new BillSplitBuilder();
    }

    public static class BillSplitBuilder {
        private BillSplit billSplit = new BillSplit();
        public BillSplitBuilder databaseId(Long id) { billSplit.databaseId = id; return this; }
        public BillSplitBuilder billName(String billName) { billSplit.billName = billName; return this; }
        public BillSplitBuilder totalAmount(double totalAmount) { billSplit.totalAmount = totalAmount; return this; }
        public BillSplitBuilder creator(User creator) { billSplit.creator = creator; return this; }
        public BillSplitBuilder creatorBank(Bank creatorBank) { billSplit.creatorBank = creatorBank; return this; }
        public BillSplitBuilder status(String status) { billSplit.status = status; return this; }
        public BillSplitBuilder createdAt(Date createdAt) { billSplit.createdAt = createdAt; return this; }
        public BillSplit build() { return billSplit; }
    }

    public Long getDatabaseId() { return databaseId; }
    public void setDatabaseId(Long databaseId) { this.databaseId = databaseId; }
    public String getBillName() { return billName; }
    public void setBillName(String billName) { this.billName = billName; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }
    public Bank getCreatorBank() { return creatorBank; }
    public void setCreatorBank(Bank creatorBank) { this.creatorBank = creatorBank; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public List<BillSplitParticipant> getParticipants() { return participants; }
    public void setParticipants(List<BillSplitParticipant> participants) { this.participants = participants; }
}
