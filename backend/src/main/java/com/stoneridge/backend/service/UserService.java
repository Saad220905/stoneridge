package com.stoneridge.backend.service;

import com.stoneridge.backend.dto.SignUpRequest;
import com.stoneridge.backend.dto.UserDTO;
import com.stoneridge.backend.exception.BadRequestException;
import com.stoneridge.backend.model.User;
import com.stoneridge.backend.repository.UserRepository;
import com.stoneridge.backend.util.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final DwollaService dwollaService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, DwollaService dwollaService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.dwollaService = dwollaService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDTO createEncryptedUserDocument(String userId, SignUpRequest userData) throws Exception {
        String email = userData.email();
        String firstName = userData.firstName();
        String lastName = userData.lastName();

        Map<String, Object> dwollaCustomerData = new HashMap<>();
        dwollaCustomerData.put("firstName", firstName);
        dwollaCustomerData.put("lastName", lastName);
        dwollaCustomerData.put("email", email);
        dwollaCustomerData.put("type", "personal");
        dwollaCustomerData.put("address1", userData.address1());
        dwollaCustomerData.put("city", userData.city());
        dwollaCustomerData.put("state", userData.state());
        dwollaCustomerData.put("postalCode", userData.postalCode());
        dwollaCustomerData.put("dateOfBirth", userData.dateOfBirth());
        dwollaCustomerData.put("ssn", userData.ssn());

        String dwollaCustomerUrl = dwollaService.createDwollaCustomer(dwollaCustomerData);

        if (dwollaCustomerUrl == null) {
            throw new BadRequestException("Error creating Dwolla customer.");
        }
        String dwollaCustomerId = extractCustomerIdFromUrl(dwollaCustomerUrl);

        User newUser = User.builder()
                .userId(userId)
                .email(EncryptionUtil.encrypt(email))
                .emailHash(EncryptionUtil.hash(email))
                .password(passwordEncoder.encode(userData.password()))
                .firstName(EncryptionUtil.encrypt(firstName))
                .lastName(EncryptionUtil.encrypt(lastName))
                .address1(EncryptionUtil.encrypt(userData.address1()))
                .city(EncryptionUtil.encrypt(userData.city()))
                .state(EncryptionUtil.encrypt(userData.state()))
                .postalCode(EncryptionUtil.encrypt(userData.postalCode()))
                .dateOfBirth(EncryptionUtil.encrypt(userData.dateOfBirth()))
                .ssn(EncryptionUtil.encrypt(userData.ssn()))
                .dwollaCustomerId(dwollaCustomerId)
                .dwollaCustomerUrl(dwollaCustomerUrl)
                .build();

        User savedUser = userRepository.save(newUser);
        return convertToDTO(savedUser, true);
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(String userId) {
        return userRepository.findByUserId(userId)
                .map(user -> {
                    try {
                        return convertToDTO(user, true);
                    } catch (Exception e) {
                        log.error("Error decrypting user fields for userId: {}", userId, e);
                        throw new RuntimeException("Error decrypting user fields", e);
                    }
                });
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByEmail(String rawEmail) throws Exception {
        return userRepository.findByEmailHash(EncryptionUtil.hash(rawEmail))
                .map(user -> {
                    try {
                        return convertToDTO(user, true);
                    } catch (Exception e) {
                        log.error("Error decrypting user fields for email: {}", rawEmail, e);
                        throw new RuntimeException("Error decrypting user fields", e);
                    }
                });
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserEntityByEmail(String email) throws Exception {
        return userRepository.findByEmailHash(EncryptionUtil.hash(email));
    }

    private UserDTO convertToDTO(User user, boolean decrypt) throws Exception {
        if (user == null) return null;
        
        UserDTO.UserDTOBuilder builder = UserDTO.builder()
                .userId(user.getUserId());
        
        if (decrypt) {
            String firstName = EncryptionUtil.decrypt(user.getFirstName());
            String lastName = EncryptionUtil.decrypt(user.getLastName());
            builder.email(EncryptionUtil.decrypt(user.getEmail()))
                    .firstName(firstName)
                    .lastName(lastName)
                    .name(firstName + " " + lastName)
                    .address1(EncryptionUtil.decrypt(user.getAddress1()))
                    .city(EncryptionUtil.decrypt(user.getCity()))
                    .state(EncryptionUtil.decrypt(user.getState()))
                    .postalCode(EncryptionUtil.decrypt(user.getPostalCode()))
                    .dateOfBirth(EncryptionUtil.decrypt(user.getDateOfBirth()));
        } else {
            builder.email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .name(user.getFirstName() + " " + user.getLastName())
                    .address1(user.getAddress1())
                    .city(user.getCity())
                    .state(user.getState())
                    .postalCode(user.getPostalCode())
                    .dateOfBirth(user.getDateOfBirth());
        }
        
        builder.dwollaCustomerId(user.getDwollaCustomerId())
                .dwollaCustomerUrl(user.getDwollaCustomerUrl());
        return builder.build();
    }

    private String extractCustomerIdFromUrl(String url) {
        if (url == null || url.isEmpty()) return null;
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
}
