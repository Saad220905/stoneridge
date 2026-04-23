package com.stoneridge.backend.dto;

public record SignUpRequest(
    String email,
    String password,
    String firstName,
    String lastName,
    String address1,
    String city,
    String state,
    String postalCode,
    String dateOfBirth,
    String ssn
) {
    public static SignUpRequestBuilder builder() {
        return new SignUpRequestBuilder();
    }

    public static class SignUpRequestBuilder {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String address1;
        private String city;
        private String state;
        private String postalCode;
        private String dateOfBirth;
        private String ssn;

        public SignUpRequestBuilder email(String email) { this.email = email; return this; }
        public SignUpRequestBuilder password(String password) { this.password = password; return this; }
        public SignUpRequestBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public SignUpRequestBuilder lastName(String lastName) { this.lastName = lastName; return this; }
        public SignUpRequestBuilder address1(String address1) { this.address1 = address1; return this; }
        public SignUpRequestBuilder city(String city) { this.city = city; return this; }
        public SignUpRequestBuilder state(String state) { this.state = state; return this; }
        public SignUpRequestBuilder postalCode(String postalCode) { this.postalCode = postalCode; return this; }
        public SignUpRequestBuilder dateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
        public SignUpRequestBuilder ssn(String ssn) { this.ssn = ssn; return this; }

        public SignUpRequest build() {
            return new SignUpRequest(email, password, firstName, lastName, address1, city, state, postalCode, dateOfBirth, ssn);
        }
    }
}
