package com.stoneridge.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stoneridge.backend.config.PasswordConfig;
import com.stoneridge.backend.dto.BillSplitDTO;
import com.stoneridge.backend.dto.BillSplitParticipantDTO;
import com.stoneridge.backend.dto.CreateBillSplitRequest;
import com.stoneridge.backend.security.JwtAuthFilter;
import com.stoneridge.backend.security.JwtUtil;
import com.stoneridge.backend.service.BillSplitService;
import com.stoneridge.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BillSplitController.class)
@Import(PasswordConfig.class)
public class BillSplitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BillSplitService billSplitService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @org.springframework.context.annotation.Bean
        public JwtAuthFilter jwtAuthFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
            return new JwtAuthFilter(jwtUtil, userDetailsService) {
                @Override
                protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, 
                                                jakarta.servlet.http.HttpServletResponse response, 
                                                jakarta.servlet.FilterChain filterChain) 
                        throws jakarta.servlet.ServletException, java.io.IOException {
                    filterChain.doFilter(request, response);
                }
            };
        }
    }

    private BillSplitDTO mockBillSplitDTO;
    private BillSplitParticipantDTO mockParticipantDTO;

    @BeforeEach
    public void setUp() {
        mockParticipantDTO = new BillSplitParticipantDTO(
                200L,
                "friend@example.com",
                "PENDING",
                50.0,
                null,
                100L,
                "Office Dinner",
                "Alice Smith",
                "creator@example.com"
        );

        mockBillSplitDTO = new BillSplitDTO(
                100L,
                "Office Dinner",
                100.0,
                "PENDING",
                new Date(),
                "creator-uid",
                "Alice Smith",
                10L,
                List.of(mockParticipantDTO)
        );
    }

    @Test
    @WithMockUser(username = "creator@example.com")
    public void testCreateBillSplit_Success() throws Exception {
        CreateBillSplitRequest request = new CreateBillSplitRequest(
                "Office Dinner",
                100.0,
                "10",
                List.of(new CreateBillSplitRequest.ParticipantRequest("friend@example.com", 50.0))
        );

        when(billSplitService.createBillSplit(eq("creator@example.com"), any(CreateBillSplitRequest.class)))
                .thenReturn(mockBillSplitDTO);

        mockMvc.perform(post("/api/billsplits/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.billName").value("Office Dinner"))
                .andExpect(jsonPath("$.totalAmount").value(100.0))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.participants[0].email").value("friend@example.com"));
    }

    @Test
    @WithMockUser(username = "friend@example.com")
    public void testGetPendingSplits_Success() throws Exception {
        when(billSplitService.getPendingSplits("friend@example.com"))
                .thenReturn(List.of(mockParticipantDTO));

        mockMvc.perform(get("/api/billsplits/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("friend@example.com"))
                .andExpect(jsonPath("$[0].amount").value(50.0))
                .andExpect(jsonPath("$[0].billName").value("Office Dinner"));
    }

    @Test
    @WithMockUser(username = "creator@example.com")
    public void testGetCreatedSplits_Success() throws Exception {
        when(billSplitService.getCreatedSplits("creator@example.com"))
                .thenReturn(List.of(mockBillSplitDTO));

        mockMvc.perform(get("/api/billsplits/created"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].billName").value("Office Dinner"))
                .andExpect(jsonPath("$[0].totalAmount").value(100.0));
    }

    @Test
    @WithMockUser(username = "friend@example.com")
    public void testPayBillSplit_Success() throws Exception {
        BillSplitParticipantDTO paidParticipant = new BillSplitParticipantDTO(
                200L,
                "friend@example.com",
                "PAID",
                50.0,
                new Date(),
                100L,
                "Office Dinner",
                "Alice Smith",
                "creator@example.com"
        );

        when(billSplitService.payBillSplit(eq("friend@example.com"), eq(200L), eq("20")))
                .thenReturn(paidParticipant);

        mockMvc.perform(post("/api/billsplits/pay/200")
                        .with(csrf())
                        .param("sourceBankId", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.amount").value(50.0));
    }
}
