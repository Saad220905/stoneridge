package com.stoneridge.backend.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_database_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double targetAmount;

    @Column(nullable = false)
    private double currentAmount;

    @Column(nullable = false)
    private String category; // e.g., 'Travel', 'Emergency', 'Purchase'

    @Column(nullable = false)
    private Date targetDate;

    private String status; // 'active', 'completed', 'cancelled'

    public Goal() {}

    public Goal(User user, String name, double targetAmount, double currentAmount, String category, Date targetDate, String status) {
        this.user = user;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.category = category;
        this.targetDate = targetDate;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Date getTargetDate() { return targetDate; }
    public void setTargetDate(Date targetDate) { this.targetDate = targetDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
