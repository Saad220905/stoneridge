package com.stoneridge.backend.repository;

import com.stoneridge.backend.model.Goal;
import com.stoneridge.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUser(User user);
    List<Goal> findByUserAndStatus(User user, String status);
}
