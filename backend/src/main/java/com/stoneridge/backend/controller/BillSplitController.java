package com.stoneridge.backend.controller;

import com.stoneridge.backend.dto.BillSplitDTO;
import com.stoneridge.backend.dto.BillSplitParticipantDTO;
import com.stoneridge.backend.dto.CreateBillSplitRequest;
import com.stoneridge.backend.service.BillSplitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billsplits")
public class BillSplitController {

    private static final Logger log = LoggerFactory.getLogger(BillSplitController.class);
    private final BillSplitService billSplitService;

    public BillSplitController(BillSplitService billSplitService) {
        this.billSplitService = billSplitService;
    }

    @PostMapping("/create")
    public ResponseEntity<BillSplitDTO> createBillSplit(
            Authentication authentication,
            @RequestBody CreateBillSplitRequest request) throws Exception {
        String email = authentication.getName();
        log.info("User {} initiating split bill request", email);
        BillSplitDTO billSplit = billSplitService.createBillSplit(email, request);
        return ResponseEntity.ok(billSplit);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<BillSplitParticipantDTO>> getPendingSplits(Authentication authentication) throws Exception {
        String email = authentication.getName();
        log.info("User {} fetching pending split bills", email);
        List<BillSplitParticipantDTO> pending = billSplitService.getPendingSplits(email);
        return ResponseEntity.ok(pending);
    }

    @GetMapping("/created")
    public ResponseEntity<List<BillSplitDTO>> getCreatedSplits(Authentication authentication) throws Exception {
        String email = authentication.getName();
        log.info("User {} fetching created split bills", email);
        List<BillSplitDTO> created = billSplitService.getCreatedSplits(email);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/pay/{participantId}")
    public ResponseEntity<BillSplitParticipantDTO> payBillSplit(
            Authentication authentication,
            @PathVariable Long participantId,
            @RequestParam String sourceBankId) throws Exception {
        String email = authentication.getName();
        log.info("User {} paying split bill participant share {}", email, participantId);
        BillSplitParticipantDTO updatedParticipant = billSplitService.payBillSplit(email, participantId, sourceBankId);
        return ResponseEntity.ok(updatedParticipant);
    }
}
