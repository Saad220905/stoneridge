package com.stoneridge.backend.dto;

import java.util.Date;
import java.util.List;

public record BillSplitDTO(
    Long databaseId,
    String billName,
    double totalAmount,
    String status,
    Date createdAt,
    String creatorId,
    String creatorName,
    Long creatorBankId,
    List<BillSplitParticipantDTO> participants
) {}
