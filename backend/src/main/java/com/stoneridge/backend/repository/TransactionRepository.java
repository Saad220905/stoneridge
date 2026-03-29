package com.stoneridge.backend.repository;

import com.stoneridge.backend.model.Transaction;
import com.stoneridge.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUserOrderByDateDesc(User user, Pageable pageable);
    List<Transaction> findByUserAndBankDatabaseIdOrderByDateDesc(User user, Long bankDatabaseId);
    List<Transaction> findByUserOrderByDateDesc(User user);
}
