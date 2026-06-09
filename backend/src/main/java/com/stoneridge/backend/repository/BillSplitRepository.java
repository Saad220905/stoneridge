package com.stoneridge.backend.repository;

import com.stoneridge.backend.model.BillSplit;
import com.stoneridge.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillSplitRepository extends JpaRepository<BillSplit, Long> {
    List<BillSplit> findByCreatorOrderByCreatedAtDesc(User creator);
}
