package com.stoneridge.backend.repository;

import com.stoneridge.backend.model.BillSplitParticipant;
import com.stoneridge.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillSplitParticipantRepository extends JpaRepository<BillSplitParticipant, Long> {
    List<BillSplitParticipant> findByEmailHashOrderByBillSplitCreatedAtDesc(String emailHash);
    List<BillSplitParticipant> findByUserOrderByBillSplitCreatedAtDesc(User user);
}
