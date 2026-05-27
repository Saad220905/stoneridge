package com.stoneridge.backend.controller;

import com.stoneridge.backend.model.Goal;
import com.stoneridge.backend.model.User;
import com.stoneridge.backend.service.GoalService;
import com.stoneridge.backend.service.UserService;
import com.stoneridge.backend.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private static final Logger log = LoggerFactory.getLogger(GoalController.class);
    private final GoalService goalService;
    private final UserService userService;

    public GoalController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Goal>> getGoals(Authentication authentication) throws Exception {
        String email = authentication.getName();
        User user = userService.findUserEntityByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
        return ResponseEntity.ok(goalService.getGoalsByUserId(user.getUserId()));
    }

    @PostMapping
    public ResponseEntity<Goal> createGoal(Authentication authentication, @RequestBody Goal goal) throws Exception {
        String email = authentication.getName();
        User user = userService.findUserEntityByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
        return ResponseEntity.ok(goalService.createGoal(user.getUserId(), goal));
    }

    @PatchMapping("/{goalId}/progress")
    public ResponseEntity<Goal> updateProgress(@PathVariable Long goalId, @RequestParam double amount, Authentication authentication) throws Exception {
        String email = authentication.getName();
        User user = userService.findUserEntityByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
        return ResponseEntity.ok(goalService.updateGoalProgress(goalId, amount, user.getUserId()));
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId, Authentication authentication) throws Exception {
        String email = authentication.getName();
        User user = userService.findUserEntityByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
        goalService.deleteGoal(goalId, user.getUserId());
        return ResponseEntity.noContent().build();
    }
}
