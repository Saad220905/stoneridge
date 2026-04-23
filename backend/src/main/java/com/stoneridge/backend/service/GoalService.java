package com.stoneridge.backend.service;

import com.stoneridge.backend.model.Goal;
import com.stoneridge.backend.model.User;
import com.stoneridge.backend.repository.GoalRepository;
import com.stoneridge.backend.repository.UserRepository;
import com.stoneridge.backend.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GoalService {

    private static final Logger log = LoggerFactory.getLogger(GoalService.class);
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalService(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Goal> getGoalsByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return goalRepository.findByUser(user);
    }

    @Transactional
    public Goal createGoal(String userId, Goal goal) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        goal.setUser(user);
        if (goal.getStatus() == null) goal.setStatus("active");
        return goalRepository.save(goal);
    }

    @Transactional
    public Goal updateGoalProgress(Long goalId, double amount) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalId));
        goal.setCurrentAmount(goal.getCurrentAmount() + amount);
        if (goal.getCurrentAmount() >= goal.getTargetAmount()) {
            goal.setStatus("completed");
        }
        return goalRepository.save(goal);
    }

    @Transactional
    public void deleteGoal(Long goalId) {
        goalRepository.deleteById(goalId);
    }
}
