package com.stoneridge.backend.dto;

import java.util.List;

public record CreateBillSplitRequest(
    String billName,
    Double totalAmount,
    String creatorBankId,
    List<ParticipantRequest> participants
) {
    public record ParticipantRequest(
        String email,
        Double amount
    ) {}
}
