package com.stoneridge.backend.service;

import com.stoneridge.backend.dto.SignUpRequest;
import com.stoneridge.backend.dto.UserDTO;
import com.stoneridge.backend.exception.ResourceNotFoundException;
import com.stoneridge.backend.model.User;
import com.stoneridge.backend.repository.UserRepository;
import com.stoneridge.backend.util.EncryptionUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for managing users.
 * Handles user creation, encryption/decryption of sensitive data, and user retrieval.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final DwollaService dwollaService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, DwollaService dwollaService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.dwollaService = dwollaService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO createEncryptedUserDocument(String userId, SignUpRequest userData) throws Exception {
        String email = userData.getEmail();
        String firstName = userData.getFirstName();
        String lastName = userData.getLastName();

        Map<String, Object> dwollaCustomerData = new HashMap<>();
        dwollaCustomerData.put("firstName", firstName);
        dwollaCustomerData.put("lastName", lastName);
        dwollaCustomerData.put("email", email);
        dwollaCustomerData.put("type", "personal");
        dwollaCustomerData.put("address1", userData.getAddress1());
        dwollaCustomerData.put("city", userData.getCity());
        dwollaCustomerData.put("state", userData.getState());
        dwollaCustomerData.put("postalCode", userData.getPostalCode());
        dwollaCustomerData.put("dateOfBirth", userData.getDateOfBirth());
        dwollaCustomerData.put("ssn", userData.getSsn());

        String dwollaCustomerUrl = dwollaService.createDwollaCustomer(dwollaCustomerData);

        if (dwollaCustomerUrl == null) {
            throw new Exception("Error creating Dwolla customer.");
        }
        String dwollaCustomerId = extractCustomerIdFromUrl(dwollaCustomerUrl);

        User newUser = new User();
        newUser.setUserId(userId);
        newUser.setEmail(EncryptionUtil.encrypt(email));
        newUser.setEmailHash(EncryptionUtil.hash(email));
        newUser.setPassword(passwordEncoder.encode(userData.getPassword()));
        newUser.setFirstName(EncryptionUtil.encrypt(firstName));
        newUser.setLastName(EncryptionUtil.encrypt(lastName));
        newUser.setAddress1(EncryptionUtil.encrypt(userData.getAddress1()));
        newUser.setCity(EncryptionUtil.encrypt(userData.getCity()));
        newUser.setState(EncryptionUtil.encrypt(userData.getState()));
        newUser.setPostalCode(EncryptionUtil.encrypt(userData.getPostalCode()));
        newUser.setDateOfBirth(EncryptionUtil.encrypt(userData.getDateOfBirth()));
        newUser.setSsn(EncryptionUtil.encrypt(userData.getSsn()));
        newUser.setDwollaCustomerId(dwollaCustomerId);
        newUser.setDwollaCustomerUrl(dwollaCustomerUrl);

        User savedUser = userRepository.save(newUser);
        return convertToDTO(decryptUserFields(savedUser));
    }

    public Optional<UserDTO> getUserById(String userId) throws Exception {
        return userRepository.findByUserId(userId)
                .map(user -> {
                    try {
                        return convertToDTO(decryptUserFields(user));
                    } catch (Exception e) {
                        throw new RuntimeException("Error decrypting user fields", e);
                    }
                });
    }

    public Optional<UserDTO> getUserByEmail(String rawEmail) throws Exception {
        return userRepository.findByEmailHash(EncryptionUtil.hash(rawEmail))
                .map(user -> {
                    try {
                        return convertToDTO(decryptUserFields(user));
                    } catch (Exception e) {
                        throw new RuntimeException("Error decrypting user fields", e);
                    }
                });
    }

    public Optional<User> findUserEntityByEmail(String email) throws Exception {
        return userRepository.findByEmailHash(EncryptionUtil.hash(email));
    }

    private User decryptUserFields(User user) throws Exception {
        if (user == null) return null;
        user.setEmail(EncryptionUtil.decrypt(user.getEmail()));
        user.setFirstName(EncryptionUtil.decrypt(user.getFirstName()));
        user.setLastName(EncryptionUtil.decrypt(user.getLastName()));
        user.setAddress1(EncryptionUtil.decrypt(user.getAddress1()));
        user.setCity(EncryptionUtil.decrypt(user.getCity()));
        user.setState(EncryptionUtil.decrypt(user.getState()));
        user.setPostalCode(EncryptionUtil.decrypt(user.getPostalCode()));
        user.setDateOfBirth(EncryptionUtil.decrypt(user.getDateOfBirth()));
        user.setSsn(EncryptionUtil.decrypt(user.getSsn()));
        return user;
    }

    private UserDTO convertToDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAddress1(user.getAddress1());
        dto.setCity(user.getCity());
        dto.setState(user.getState());
        dto.setPostalCode(user.getPostalCode());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setDwollaCustomerId(user.getDwollaCustomerId());
        dto.setDwollaCustomerUrl(user.getDwollaCustomerUrl());
        return dto;
    }

    private String extractCustomerIdFromUrl(String url) {
        if (url == null || url.isEmpty()) return null;
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
}
