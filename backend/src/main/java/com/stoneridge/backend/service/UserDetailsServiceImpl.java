package com.stoneridge.backend.service;

import com.stoneridge.backend.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService; // Inject UserService to get decrypted email

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            // Find the user entity by the (unencrypted) email provided by Spring Security
            // The findUserEntityByEmail returns the User entity with encrypted fields
            User userEntity = userService.findUserEntityByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            // The password field in our User entity is intended to be the HASHED password.
            // No need to decrypt email here as the lookup is on encrypted email.
            // The email in the UserDetails will be the one passed to loadUserByUsername.
            return new org.springframework.security.core.userdetails.User(
                    email, // Use the provided email (unencrypted)
                    userEntity.getPassword(), // This should be the hashed password from DB
                    new ArrayList<>() // Roles/Authorities can be added here
            );
        } catch (Exception e) {
            throw new UsernameNotFoundException("Error loading user by email: " + email, e);
        }
    }
}
