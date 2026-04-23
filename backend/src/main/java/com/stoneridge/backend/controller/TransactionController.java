package com.stoneridge.backend.controller;

import com.stoneridge.backend.dto.TransactionDTO;
import com.stoneridge.backend.dto.TransactionRequest;
import com.stoneridge.backend.exception.ResourceNotFoundException;
import com.stoneridge.backend.model.User;
import com.stoneridge.backend.service.TransactionService;
import com.stoneridge.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String bankId,
            Authentication authentication
    ) throws Exception {
        String userEmail = authentication.getName();
        User user = userService.findUserEntityByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(transactionService.getTransactions(user.getUserId(), bankId, page, pageSize));
    }

    @PostMapping("/create")
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionRequest transactionData, Authentication authentication) throws Exception {
        String userEmail = authentication.getName();
        User user = userService.findUserEntityByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        TransactionDTO transaction = transactionService.createTransaction(user.getUserId(), transactionData);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }
}
