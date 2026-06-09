package com.stoneridge.backend.service;

import com.stoneridge.backend.dto.*;
import com.stoneridge.backend.exception.BadRequestException;
import com.stoneridge.backend.exception.ResourceNotFoundException;
import com.stoneridge.backend.model.*;
import com.stoneridge.backend.repository.*;
import com.stoneridge.backend.util.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillSplitServiceTest {

    @Mock
    private BillSplitRepository billSplitRepository;
    @Mock
    private BillSplitParticipantRepository billSplitParticipantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BankRepository bankRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private DwollaService dwollaService;
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private BillSplitService billSplitService;

    private User creator;
    private User friend;
    private Bank creatorBank;
    private Bank friendBank;

    @BeforeEach
    public void setUp() throws Exception {
        // Prepare some mock users and banks
        creator = User.builder()
                .databaseId(1L)
                .userId("creator-uid")
                .email(EncryptionUtil.encrypt("creator@example.com"))
                .emailHash(EncryptionUtil.hash("creator@example.com"))
                .firstName(EncryptionUtil.encrypt("Alice"))
                .lastName(EncryptionUtil.encrypt("Smith"))
                .build();

        friend = User.builder()
                .databaseId(2L)
                .userId("friend-uid")
                .email(EncryptionUtil.encrypt("friend@example.com"))
                .emailHash(EncryptionUtil.hash("friend@example.com"))
                .firstName(EncryptionUtil.encrypt("Bob"))
                .lastName(EncryptionUtil.encrypt("Jones"))
                .build();

        creatorBank = Bank.builder()
                .databaseId(10L)
                .user(creator)
                .bankId("bank-10")
                .accountId("acc-10")
                .fundingSourceUrl("https://api.dwolla.com/funding-sources/10")
                .build();

        friendBank = Bank.builder()
                .databaseId(20L)
                .user(friend)
                .bankId("bank-20")
                .accountId("acc-20")
                .fundingSourceUrl("https://api.dwolla.com/funding-sources/20")
                .build();
    }

    @Test
    public void testCreateBillSplit_Success() throws Exception {
        // Given
        CreateBillSplitRequest request = new CreateBillSplitRequest(
                "Lunch split",
                30.0,
                "10",
                List.of(new CreateBillSplitRequest.ParticipantRequest("friend@example.com", 15.0))
        );

        when(userRepository.findByEmailHash(eq(EncryptionUtil.hash("creator@example.com"))))
                .thenReturn(Optional.of(creator));
        when(bankRepository.findById(10L)).thenReturn(Optional.of(creatorBank));
        when(userRepository.findByEmailHash(eq(EncryptionUtil.hash("friend@example.com"))))
                .thenReturn(Optional.of(friend));

        BillSplit savedSplit = BillSplit.builder()
                .databaseId(100L)
                .billName("Lunch split")
                .totalAmount(30.0)
                .creator(creator)
                .creatorBank(creatorBank)
                .status("PENDING")
                .createdAt(new Date())
                .build();

        when(billSplitRepository.save(any(BillSplit.class))).thenReturn(savedSplit);

        BillSplitParticipant participant = BillSplitParticipant.builder()
                .databaseId(200L)
                .billSplit(savedSplit)
                .email(EncryptionUtil.encrypt("friend@example.com"))
                .emailHash(EncryptionUtil.hash("friend@example.com"))
                .amount(15.0)
                .status("PENDING")
                .user(friend)
                .build();

        when(billSplitParticipantRepository.save(any(BillSplitParticipant.class))).thenReturn(participant);

        // When
        BillSplitDTO result = billSplitService.createBillSplit("creator@example.com", request);

        // Then
        assertNotNull(result);
        assertEquals("Lunch split", result.billName());
        assertEquals(30.0, result.totalAmount());
        assertEquals("PENDING", result.status());
        assertEquals(1, result.participants().size());
        assertEquals("friend@example.com", result.participants().getFirst().email());
        assertEquals(15.0, result.participants().getFirst().amount());

        verify(billSplitRepository, times(2)).save(any(BillSplit.class));
        verify(billSplitParticipantRepository, times(1)).save(any(BillSplitParticipant.class));
    }

    @Test
    public void testCreateBillSplit_InvalidBank() throws Exception {
        CreateBillSplitRequest request = new CreateBillSplitRequest(
                "Lunch split",
                30.0,
                "10",
                List.of(new CreateBillSplitRequest.ParticipantRequest("friend@example.com", 15.0))
        );

        when(userRepository.findByEmailHash(anyString())).thenReturn(Optional.of(creator));
        when(bankRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            billSplitService.createBillSplit("creator@example.com", request);
        });
    }

    @Test
    public void testPayBillSplit_Success() throws Exception {
        // Given
        BillSplit split = BillSplit.builder()
                .databaseId(100L)
                .billName("Dinner split")
                .totalAmount(60.0)
                .creator(creator)
                .creatorBank(creatorBank)
                .status("PENDING")
                .createdAt(new Date())
                .build();

        BillSplitParticipant participant = BillSplitParticipant.builder()
                .databaseId(200L)
                .billSplit(split)
                .email(EncryptionUtil.encrypt("friend@example.com"))
                .emailHash(EncryptionUtil.hash("friend@example.com"))
                .amount(30.0)
                .status("PENDING")
                .user(friend)
                .build();
        split.setParticipants(List.of(participant));

        when(billSplitParticipantRepository.findById(200L)).thenReturn(Optional.of(participant));
        when(userRepository.findByEmailHash(eq(EncryptionUtil.hash("friend@example.com"))))
                .thenReturn(Optional.of(friend));
        when(bankRepository.findById(20L)).thenReturn(Optional.of(friendBank));

        when(dwollaService.createTransfer(anyString(), anyString(), anyDouble()))
                .thenReturn("https://api.dwolla.com/transfers/transfer-111");

        Transaction tx = Transaction.builder()
                .databaseId(500L)
                .amount(30.0)
                .status("Success")
                .build();

        TransactionDTO txDto = TransactionDTO.builder()
                .databaseId(500L)
                .amount(30.0)
                .status("Success")
                .build();

        when(transactionService.createTransaction(eq("friend-uid"), any(TransactionRequest.class)))
                .thenReturn(txDto);
        when(transactionRepository.findById(500L)).thenReturn(Optional.of(tx));

        when(billSplitParticipantRepository.save(any(BillSplitParticipant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BillSplitParticipantDTO result = billSplitService.payBillSplit("friend@example.com", 200L, "20");

        // Then
        assertNotNull(result);
        assertEquals("PAID", result.status());
        assertEquals(30.0, result.amount());
        assertEquals("Dinner split", result.billName());

        verify(dwollaService, times(1)).createTransfer(
                eq(friendBank.getFundingSourceUrl()),
                eq(creatorBank.getFundingSourceUrl()),
                eq(30.0)
        );
        verify(transactionService, times(1)).createTransaction(eq("friend-uid"), any(TransactionRequest.class));
        verify(billSplitRepository, times(1)).save(split);
        assertEquals("COMPLETED", split.getStatus());
    }

    @Test
    public void testPayBillSplit_UnauthorizedPayer() throws Exception {
        BillSplit split = BillSplit.builder()
                .databaseId(100L)
                .creator(creator)
                .status("PENDING")
                .build();

        BillSplitParticipant participant = BillSplitParticipant.builder()
                .databaseId(200L)
                .billSplit(split)
                .email(EncryptionUtil.encrypt("friend@example.com"))
                .emailHash(EncryptionUtil.hash("friend@example.com"))
                .amount(30.0)
                .status("PENDING")
                .build();

        when(billSplitParticipantRepository.findById(200L)).thenReturn(Optional.of(participant));

        assertThrows(BadRequestException.class, () -> {
            billSplitService.payBillSplit("stranger@example.com", 200L, "20");
        });
    }
}
