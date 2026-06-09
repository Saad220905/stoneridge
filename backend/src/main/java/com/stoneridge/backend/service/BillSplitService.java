package com.stoneridge.backend.service;

import com.stoneridge.backend.dto.*;
import com.stoneridge.backend.exception.BadRequestException;
import com.stoneridge.backend.exception.ResourceNotFoundException;
import com.stoneridge.backend.model.*;
import com.stoneridge.backend.repository.*;
import com.stoneridge.backend.util.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BillSplitService {

    private static final Logger log = LoggerFactory.getLogger(BillSplitService.class);

    private final BillSplitRepository billSplitRepository;
    private final BillSplitParticipantRepository billSplitParticipantRepository;
    private final UserRepository userRepository;
    private final BankRepository bankRepository;
    private final TransactionRepository transactionRepository;
    private final DwollaService dwollaService;
    private final TransactionService transactionService;

    public BillSplitService(
            BillSplitRepository billSplitRepository,
            BillSplitParticipantRepository billSplitParticipantRepository,
            UserRepository userRepository,
            BankRepository bankRepository,
            TransactionRepository transactionRepository,
            DwollaService dwollaService,
            TransactionService transactionService) {
        this.billSplitRepository = billSplitRepository;
        this.billSplitParticipantRepository = billSplitParticipantRepository;
        this.userRepository = userRepository;
        this.bankRepository = bankRepository;
        this.transactionRepository = transactionRepository;
        this.dwollaService = dwollaService;
        this.transactionService = transactionService;
    }

    @Transactional
    public BillSplitDTO createBillSplit(String creatorEmail, CreateBillSplitRequest request) throws Exception {
        if (request.billName() == null || request.billName().isBlank()) {
            throw new BadRequestException("Bill name is required.");
        }
        if (request.totalAmount() == null || request.totalAmount() <= 0) {
            throw new BadRequestException("Total amount must be greater than zero.");
        }
        if (request.creatorBankId() == null || request.creatorBankId().isBlank()) {
            throw new BadRequestException("Creator bank account ID is required.");
        }
        if (request.participants() == null || request.participants().isEmpty()) {
            throw new BadRequestException("At least one participant is required.");
        }

        User creator = userRepository.findByEmailHash(EncryptionUtil.hash(creatorEmail))
                .orElseThrow(() -> new ResourceNotFoundException("Creator user not found."));

        Long bankDbId;
        try {
            bankDbId = Long.valueOf(request.creatorBankId());
        } catch (NumberFormatException e) {
            Bank bankByPlaid = bankRepository.findByAppwriteItemId(request.creatorBankId())
                    .orElseThrow(() -> new ResourceNotFoundException("Creator bank not found."));
            bankDbId = bankByPlaid.getDatabaseId();
        }

        Bank creatorBank = bankRepository.findById(bankDbId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator bank not found."));

        if (!creatorBank.getUser().getUserId().equals(creator.getUserId())) {
            throw new BadRequestException("Creator bank does not belong to the creator user.");
        }

        BillSplit billSplit = BillSplit.builder()
                .billName(request.billName())
                .totalAmount(request.totalAmount())
                .creator(creator)
                .creatorBank(creatorBank)
                .status("PENDING")
                .createdAt(new Date())
                .build();

        BillSplit savedBillSplit = billSplitRepository.save(billSplit);

        List<BillSplitParticipant> participants = new ArrayList<>();
        double totalParticipantShares = 0;

        for (CreateBillSplitRequest.ParticipantRequest partReq : request.participants()) {
            if (partReq.email() == null || partReq.email().isBlank()) {
                throw new BadRequestException("Participant email is required.");
            }
            if (partReq.amount() == null || partReq.amount() <= 0) {
                throw new BadRequestException("Participant share amount must be greater than zero.");
            }

            String hashedEmail = EncryptionUtil.hash(partReq.email());
            String encryptedEmail = EncryptionUtil.encrypt(partReq.email());

            Optional<User> participantUser = userRepository.findByEmailHash(hashedEmail);

            BillSplitParticipant participant = BillSplitParticipant.builder()
                    .billSplit(savedBillSplit)
                    .email(encryptedEmail)
                    .emailHash(hashedEmail)
                    .amount(partReq.amount())
                    .status("PENDING")
                    .user(participantUser.orElse(null))
                    .build();

            participants.add(billSplitParticipantRepository.save(participant));
            totalParticipantShares += partReq.amount();
        }

        savedBillSplit.setParticipants(participants);
        billSplitRepository.save(savedBillSplit);

        return convertToBillSplitDTO(savedBillSplit);
    }

    @Transactional(readOnly = true)
    public List<BillSplitParticipantDTO> getPendingSplits(String email) throws Exception {
        String hashedEmail = EncryptionUtil.hash(email);
        List<BillSplitParticipant> shares = billSplitParticipantRepository.findByEmailHashOrderByBillSplitCreatedAtDesc(hashedEmail);

        List<BillSplitParticipantDTO> pendingShares = new ArrayList<>();
        for (BillSplitParticipant p : shares) {
            if ("PENDING".equals(p.getStatus())) {
                pendingShares.add(convertToParticipantDTO(p));
            }
        }
        return pendingShares;
    }

    @Transactional(readOnly = true)
    public List<BillSplitDTO> getCreatedSplits(String email) throws Exception {
        User creator = userRepository.findByEmailHash(EncryptionUtil.hash(email))
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        List<BillSplit> splits = billSplitRepository.findByCreatorOrderByCreatedAtDesc(creator);
        List<BillSplitDTO> dtos = new ArrayList<>();
        for (BillSplit bs : splits) {
            dtos.add(convertToBillSplitDTO(bs));
        }
        return dtos;
    }

    @Transactional
    public BillSplitParticipantDTO payBillSplit(String payerEmail, Long participantId, String sourceBankId) throws Exception {
        BillSplitParticipant participant = billSplitParticipantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill split participant record not found."));

        String payerEmailHash = EncryptionUtil.hash(payerEmail);
        if (!participant.getEmailHash().equals(payerEmailHash)) {
            throw new BadRequestException("You are not authorized to settle this split bill share.");
        }

        if (!"PENDING".equals(participant.getStatus())) {
            throw new BadRequestException("This split share has already been paid or settled.");
        }

        User payer = userRepository.findByEmailHash(payerEmailHash)
                .orElseThrow(() -> new ResourceNotFoundException("Payer user not found."));

        Long bankDbId;
        try {
            bankDbId = Long.valueOf(sourceBankId);
        } catch (NumberFormatException e) {
            Bank bankByPlaid = bankRepository.findByAppwriteItemId(sourceBankId)
                    .orElseThrow(() -> new ResourceNotFoundException("Settle bank not found."));
            bankDbId = bankByPlaid.getDatabaseId();
        }

        Bank sourceBank = bankRepository.findById(bankDbId)
                .orElseThrow(() -> new ResourceNotFoundException("Settle bank not found."));

        if (!sourceBank.getUser().getUserId().equals(payer.getUserId())) {
            throw new BadRequestException("Specified funding source bank does not belong to the payer.");
        }

        BillSplit billSplit = participant.getBillSplit();
        Bank creatorBank = billSplit.getCreatorBank();

        if (sourceBank.getFundingSourceUrl() == null || sourceBank.getFundingSourceUrl().isBlank()) {
            throw new BadRequestException("Source bank funding source URL is missing.");
        }
        if (creatorBank.getFundingSourceUrl() == null || creatorBank.getFundingSourceUrl().isBlank()) {
            throw new BadRequestException("Destination bank funding source URL is missing.");
        }

        // 1. Call Dwolla to initiate the actual transfer
        String transferUrl = dwollaService.createTransfer(
                sourceBank.getFundingSourceUrl(),
                creatorBank.getFundingSourceUrl(),
                participant.getAmount()
        );

        if (transferUrl == null || transferUrl.isBlank()) {
            throw new BadRequestException("Dwolla transfer authorization failed.");
        }

        // 2. Create ledger transaction via TransactionService
        TransactionRequest transactionRequest = TransactionRequest.builder()
                .name("Bill Split Pay: " + billSplit.getBillName())
                .amount(participant.getAmount())
                .currency("USD")
                .category("Transfer")
                .type("debit")
                .status("Success")
                .paymentChannel("online")
                .senderId(payer.getUserId())
                .senderBankId(sourceBank.getDatabaseId().toString())
                .receiverId(billSplit.getCreator().getUserId())
                .receiverBankId(creatorBank.getDatabaseId().toString())
                .email(payerEmail)
                .build();

        TransactionDTO txDto = transactionService.createTransaction(payer.getUserId(), transactionRequest);
        Transaction transactionEntity = transactionRepository.findById(txDto.getDatabaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not created successfully."));

        // 3. Update Participant details
        participant.setStatus("PAID");
        participant.setPaidAt(new Date());
        participant.setTransaction(transactionEntity);
        participant.setUser(payer); // Settle user association if not set initially
        BillSplitParticipant updatedParticipant = billSplitParticipantRepository.save(participant);

        // 4. Update Bill Split overall status if all are paid
        boolean allPaid = billSplit.getParticipants().stream()
                .allMatch(p -> "PAID".equals(p.getStatus()));

        if (allPaid) {
            billSplit.setStatus("COMPLETED");
            billSplitRepository.save(billSplit);
        }

        return convertToParticipantDTO(updatedParticipant);
    }

    private BillSplitDTO convertToBillSplitDTO(BillSplit bs) throws Exception {
        User creator = bs.getCreator();
        String creatorName = EncryptionUtil.decrypt(creator.getFirstName()) + " " + EncryptionUtil.decrypt(creator.getLastName());
        String creatorEmail = EncryptionUtil.decrypt(creator.getEmail());

        List<BillSplitParticipantDTO> participantDTOs = new ArrayList<>();
        for (BillSplitParticipant p : bs.getParticipants()) {
            participantDTOs.add(convertToParticipantDTO(p));
        }

        return new BillSplitDTO(
                bs.getDatabaseId(),
                bs.getBillName(),
                bs.getTotalAmount(),
                bs.getStatus(),
                bs.getCreatedAt(),
                creator.getUserId(),
                creatorName,
                bs.getCreatorBank().getDatabaseId(),
                participantDTOs
        );
    }

    private BillSplitParticipantDTO convertToParticipantDTO(BillSplitParticipant p) throws Exception {
        User creator = p.getBillSplit().getCreator();
        String creatorName = EncryptionUtil.decrypt(creator.getFirstName()) + " " + EncryptionUtil.decrypt(creator.getLastName());
        String creatorEmail = EncryptionUtil.decrypt(creator.getEmail());
        String participantEmail = EncryptionUtil.decrypt(p.getEmail());

        return new BillSplitParticipantDTO(
                p.getDatabaseId(),
                participantEmail,
                p.getStatus(),
                p.getAmount(),
                p.getPaidAt(),
                p.getBillSplit().getDatabaseId(),
                p.getBillSplit().getBillName(),
                creatorName,
                creatorEmail
        );
    }
}
