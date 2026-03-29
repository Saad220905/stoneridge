package com.stoneridge.backend.controller;

import com.stoneridge.backend.dto.TransactionDTO;
import com.stoneridge.backend.dto.TransactionRequest;
import com.stoneridge.backend.exception.BadRequestException;
import com.stoneridge.backend.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling transaction-related requests.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String bankId,
            Authentication authentication
    ) throws Exception {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(transactionService.getTransactions(userEmail, bankId, page, pageSize));
    }

    @PostMapping("/create")
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionRequest transactionData, Authentication authentication) throws Exception {
        String userEmail = authentication.getName();
        TransactionDTO transaction = transactionService.createTransaction(userEmail, transactionData);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }
}
