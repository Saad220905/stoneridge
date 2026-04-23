package com.stoneridge.backend.dto;

import com.stoneridge.backend.model.User;

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

    public static UserDTOBuilder builder() {
        return new UserDTOBuilder();
    }

    public static class UserDTOBuilder {
        private UserDTO dto = new UserDTO();
        public UserDTOBuilder userId(String id) { dto.userId = id; return this; }
        public UserDTOBuilder email(String email) { dto.email = email; return this; }
        public UserDTOBuilder firstName(String fn) { dto.firstName = fn; return this; }
        public UserDTOBuilder lastName(String ln) { dto.lastName = ln; return this; }
        public UserDTOBuilder name(String name) { dto.name = name; return this; }
        public UserDTOBuilder address1(String a1) { dto.address1 = a1; return this; }
        public UserDTOBuilder city(String c) { dto.city = c; return this; }
        public UserDTOBuilder state(String s) { dto.state = s; return this; }
        public UserDTOBuilder postalCode(String pc) { dto.postalCode = pc; return this; }
        public UserDTOBuilder dateOfBirth(String dob) { dto.dateOfBirth = dob; return this; }
        public UserDTOBuilder dwollaCustomerId(String id) { dto.dwollaCustomerId = id; return this; }
        public UserDTOBuilder dwollaCustomerUrl(String url) { dto.dwollaCustomerUrl = url; return this; }
        public UserDTO build() { return dto; }
    }

    public static UserDTO fromEntity(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .name(user.getFirstName() + " " + user.getLastName())
                .address1(user.getAddress1())
                .city(user.getCity())
                .state(user.getState())
                .postalCode(user.getPostalCode())
                .dateOfBirth(user.getDateOfBirth())
                .dwollaCustomerId(user.getDwollaCustomerId())
                .dwollaCustomerUrl(user.getDwollaCustomerUrl())
                .build();
    }

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
