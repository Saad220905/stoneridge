package com.stoneridge.backend.repository;

import com.stoneridge.backend.model.Bank;
import com.stoneridge.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    List<Bank> findByUser(User user);
    Optional<Bank> findByShareableId(String shareableId);
    Optional<Bank> findByAccountIdHash(String accountIdHash);
}
