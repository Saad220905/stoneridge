package com.stoneridge.backend.controller;

import com.stoneridge.backend.dto.DwollaTransferRequest;
import com.stoneridge.backend.exception.BadRequestException;
import com.stoneridge.backend.service.DwollaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for handling Dwolla-related operations.
 */
@RestController
@RequestMapping("/api/dwolla")
public class DwollaController {

    private static final Logger log = LoggerFactory.getLogger(DwollaController.class);
    private final DwollaService dwollaService;

    public DwollaController(DwollaService dwollaService) {
        this.dwollaService = dwollaService;
    }

    @PostMapping("/create-transfer")
    public ResponseEntity<Map<String, String>> createTransfer(@RequestBody DwollaTransferRequest request, Authentication authentication) throws Exception {
        if (request.getSourceFundingSourceUrl() == null || request.getDestinationFundingSourceUrl() == null || request.getAmount() == null) {
            throw new BadRequestException("Source funding source, destination funding source, and amount are required.");
        }

        String transferUrl = dwollaService.createTransfer(
                request.getSourceFundingSourceUrl(),
                request.getDestinationFundingSourceUrl(),
                request.getAmount()
        );
        return ResponseEntity.ok(Map.of("transferUrl", transferUrl));
    }
}
