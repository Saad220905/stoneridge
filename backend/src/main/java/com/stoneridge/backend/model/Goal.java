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
    private String category;

    @Column(nullable = false)
    private Date targetDate;

    private String status;

    public Goal() {}

    public static GoalBuilder builder() {
        return new GoalBuilder();
    }

    public static class GoalBuilder {
        private Goal goal = new Goal();
        public GoalBuilder id(Long id) { goal.id = id; return this; }
        public GoalBuilder user(User user) { goal.user = user; return this; }
        public GoalBuilder name(String name) { goal.name = name; return this; }
        public GoalBuilder targetAmount(double amount) { goal.targetAmount = amount; return this; }
        public GoalBuilder currentAmount(double amount) { goal.currentAmount = amount; return this; }
        public GoalBuilder category(String category) { goal.category = category; return this; }
        public GoalBuilder targetDate(Date date) { goal.targetDate = date; return this; }
        public GoalBuilder status(String status) { goal.status = status; return this; }
        public Goal build() { return goal; }
    }

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
