package com.stoneridge.backend.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long databaseId; // Primary key for JPA

    @Column(unique = true, nullable = false)
    private String userId; // Unique ID (formerly from Appwrite)

    @Column(nullable = false)
    private String email; // Stored encrypted

    @Column(unique = true, nullable = false)
    private String emailHash; // Deterministic hash for lookups

    @Column(nullable = false)
    private String password; // Stored hashed

    private String firstName; // Stored encrypted
    private String lastName; // Stored encrypted
    private String address1; // Stored encrypted
    private String city;     // Stored encrypted
    private String state;    // Stored encrypted
    private String postalCode; // Stored encrypted
    private String dateOfBirth; // Stored encrypted
    private String ssn;      // Stored encrypted

    private String dwollaCustomerId;
    private String dwollaCustomerUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bank> banks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    // No-argument constructor
    public User() {
    }

    // All-argument constructor
    public User(Long databaseId, String userId, String email, String emailHash, String password, String firstName, String lastName,
                String address1, String city, String state, String postalCode, String dateOfBirth, String ssn,
                String dwollaCustomerId, String dwollaCustomerUrl) {
        this.databaseId = databaseId;
        this.userId = userId;
        this.email = email;
        this.emailHash = emailHash;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address1 = address1;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.dateOfBirth = dateOfBirth;
        this.ssn = ssn;
        this.dwollaCustomerId = dwollaCustomerId;
        this.dwollaCustomerUrl = dwollaCustomerUrl;
    }


    // Manual getters and setters
    public Long getDatabaseId() { return databaseId; }
    public void setDatabaseId(Long databaseId) { this.databaseId = databaseId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEmailHash() { return emailHash; }
    public void setEmailHash(String emailHash) { this.emailHash = emailHash; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }
    public String getDwollaCustomerId() { return dwollaCustomerId; }
    public void setDwollaCustomerId(String dwollaCustomerId) { this.dwollaCustomerId = dwollaCustomerId; }
    public String getDwollaCustomerUrl() { return dwollaCustomerUrl; }
    public void setDwollaCustomerUrl(String dwollaCustomerUrl) { this.dwollaCustomerUrl = dwollaCustomerUrl; }
    public List<Bank> getBanks() { return banks; }
    public void setBanks(List<Bank> banks) { this.banks = banks; }
    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
}
