package com.stoneridge.backend.controller;

import com.stoneridge.backend.dto.AuthRequest;
import com.stoneridge.backend.dto.AuthResponse;
import com.stoneridge.backend.dto.SignUpRequest;
import com.stoneridge.backend.dto.UserDTO;
import com.stoneridge.backend.exception.BadRequestException;
import com.stoneridge.backend.exception.ResourceNotFoundException;
import com.stoneridge.backend.exception.UnauthorizedException;
import com.stoneridge.backend.security.JwtUtil;
import com.stoneridge.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for handling authentication-related requests.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthController(UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@RequestBody SignUpRequest userData) {
        try {
            String generatedUserId = UUID.randomUUID().toString();
            UserDTO newUser = userService.createEncryptedUserDocument(generatedUserId, userData);
            
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userData.getEmail(), userData.getPassword())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(userData.getEmail());
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, newUser));
        } catch (Exception e) {
            log.error("Exception during signUp: {}", e.getMessage(), e);
            throw new BadRequestException("Failed to register user: " + e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
                String token = jwtUtil.generateToken(userDetails);
                UserDTO user = userService.getUserByEmail(authRequest.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + authRequest.getEmail()));
                return ResponseEntity.ok(new AuthResponse(token, user));
            } else {
                throw new UnauthorizedException("Authentication failed.");
            }
        } catch (ResourceNotFoundException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception during signIn: {}", e.getMessage(), e);
            throw new UnauthorizedException("Invalid credentials.");
        }
    }

    @GetMapping("/user")
    public ResponseEntity<UserDTO> getLoggedInUser(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            UserDTO user = userService.getUserByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found."));
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception during getLoggedInUser: {}", e.getMessage(), e);
            throw new BadRequestException("Failed to retrieve user data.");
        }
    }
}
