package com.stoneridge.backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long databaseId;

    @Column(unique = true, nullable = false)
    private String userId;

    @Column(nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String emailHash;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;
    private String address1;
    private String city;
    private String state;
    private String postalCode;
    private String dateOfBirth;
    private String ssn;

    private String dwollaCustomerId;
    private String dwollaCustomerUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bank> banks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    public User() {}

    public User(Long databaseId, String userId, String email, String emailHash, String password, String firstName, String lastName, String address1, String city, String state, String postalCode, String dateOfBirth, String ssn, String dwollaCustomerId, String dwollaCustomerUrl) {
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

    // Static builder-like method for convenience
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private User user = new User();
        public UserBuilder databaseId(Long id) { user.databaseId = id; return this; }
        public UserBuilder userId(String id) { user.userId = id; return this; }
        public UserBuilder email(String email) { user.email = email; return this; }
        public UserBuilder emailHash(String hash) { user.emailHash = hash; return this; }
        public UserBuilder password(String pw) { user.password = pw; return this; }
        public UserBuilder firstName(String fn) { user.firstName = fn; return this; }
        public UserBuilder lastName(String ln) { user.lastName = ln; return this; }
        public UserBuilder address1(String a1) { user.address1 = a1; return this; }
        public UserBuilder city(String c) { user.city = c; return this; }
        public UserBuilder state(String s) { user.state = s; return this; }
        public UserBuilder postalCode(String pc) { user.postalCode = pc; return this; }
        public UserBuilder dateOfBirth(String dob) { user.dateOfBirth = dob; return this; }
        public UserBuilder ssn(String ssn) { user.ssn = ssn; return this; }
        public UserBuilder dwollaCustomerId(String id) { user.dwollaCustomerId = id; return this; }
        public UserBuilder dwollaCustomerUrl(String url) { user.dwollaCustomerUrl = url; return this; }
        public User build() { return user; }
    }

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
