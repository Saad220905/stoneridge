package com.stoneridge.backend.controller;

import com.stoneridge.backend.dto.BankDTO;
import com.stoneridge.backend.exception.ResourceNotFoundException;
import com.stoneridge.backend.model.User;
import com.stoneridge.backend.service.BankService;
import com.stoneridge.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for managing bank-related operations.
 */
@RestController
@RequestMapping("/api/bank")
public class BankController {

    private static final Logger log = LoggerFactory.getLogger(BankController.class);
    private final BankService bankService;
    private final UserService userService;

    public BankController(BankService bankService, UserService userService) {
        this.bankService = bankService;
        this.userService = userService;
    }

    @GetMapping("/banks")
    public ResponseEntity<List<BankDTO>> getBanks(Authentication authentication) throws Exception {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(bankService.getBanks(userEmail));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankDTO> getBank(@PathVariable Long id) {
        return bankService.getBank(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Bank not found with ID: " + id));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<BankDTO> getBankByAccountId(@PathVariable String accountId, Authentication authentication) throws Exception {
        String userEmail = authentication.getName();
        return bankService.getBankByAccountId(accountId, userEmail)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Bank not found for account ID: " + accountId));
    }

    @PostMapping("/exchange-token")
    public ResponseEntity<?> exchangePublicToken(@RequestBody Map<String, String> request, Authentication authentication) throws Exception {
        String publicToken = request.get("publicToken");
        String userEmail = authentication.getName();
        bankService.exchangePublicToken(userEmail, publicToken);
        return ResponseEntity.ok(Map.of("message", "Public token exchanged successfully."));
    }

    @GetMapping("/create-link-token")
    public ResponseEntity<Map<String, String>> createLinkToken(Authentication authentication) throws Exception {
        String userEmail = authentication.getName();
        User user = userService.findUserEntityByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String linkToken = bankService.createLinkToken(user);
        return ResponseEntity.ok(Map.of("linkToken", linkToken));
    }
}
