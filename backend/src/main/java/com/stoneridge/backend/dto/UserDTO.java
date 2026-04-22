package com.stoneridge.backend.dto;

import com.stoneridge.backend.model.User;

/**
 * Data Transfer Object for User information.
 * Excludes sensitive fields like password, emailHash, and ssn.
 */
public class UserDTO {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String name;
    private String address1;
    private String city;
    private String state;
    private String postalCode;
    private String dateOfBirth;
    private String dwollaCustomerId;
    private String dwollaCustomerUrl;

    public UserDTO() {}

    public static UserDTO fromEntity(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setName(user.getFirstName() + " " + user.getLastName());
        dto.setAddress1(user.getAddress1());
        dto.setCity(user.getCity());
        dto.setState(user.getState());
        dto.setPostalCode(user.getPostalCode());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setDwollaCustomerId(user.getDwollaCustomerId());
        dto.setDwollaCustomerUrl(user.getDwollaCustomerUrl());
        return dto;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
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
    public String getDwollaCustomerId() { return dwollaCustomerId; }
    public void setDwollaCustomerId(String dwollaCustomerId) { this.dwollaCustomerId = dwollaCustomerId; }
    public String getDwollaCustomerUrl() { return dwollaCustomerUrl; }
    public void setDwollaCustomerUrl(String dwollaCustomerUrl) { this.dwollaCustomerUrl = dwollaCustomerUrl; }
}
