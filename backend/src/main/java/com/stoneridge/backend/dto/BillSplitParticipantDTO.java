package com.stoneridge.backend.dto;

import java.util.Date;

public record BillSplitParticipantDTO(
    Long databaseId,
    String email,
    String status,
    double amount,
    Date paidAt,
    Long billSplitId,
    String billName,
    String creatorName,
    String creatorEmail
) {}
