package com.stoneridge.backend.service;

import com.stoneridge.backend.model.Goal;
import com.stoneridge.backend.model.User;
import com.stoneridge.backend.repository.GoalRepository;
import com.stoneridge.backend.repository.UserRepository;
import com.stoneridge.backend.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalService(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    public List<Goal> getGoalsByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return goalRepository.findByUser(user);
    }

    public Goal createGoal(String userId, Goal goal) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        goal.setUser(user);
        if (goal.getStatus() == null) goal.setStatus("active");
        return goalRepository.save(goal);
    }

    public Goal updateGoalProgress(Long goalId, double amount) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalId));
        goal.setCurrentAmount(goal.getCurrentAmount() + amount);
        if (goal.getCurrentAmount() >= goal.getTargetAmount()) {
            goal.setStatus("completed");
        }
        return goalRepository.save(goal);
    }

    public void deleteGoal(Long goalId) {
        goalRepository.deleteById(goalId);
    }
}
