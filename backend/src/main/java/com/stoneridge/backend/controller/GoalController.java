package com.stoneridge.backend.controller;

import com.stoneridge.backend.model.Goal;
import com.stoneridge.backend.model.User;
import com.stoneridge.backend.service.GoalService;
import com.stoneridge.backend.service.UserService;
import com.stoneridge.backend.security.JwtUtil;
import com.stoneridge.backend.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public GoalController(GoalService goalService, UserService userService, JwtUtil jwtUtil) {
        this.goalService = goalService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<Goal>> getGoals(@RequestHeader("Authorization") String token) throws Exception {
        String email = jwtUtil.extractUsername(token.substring(7));
        User user = userService.findUserEntityByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
        return ResponseEntity.ok(goalService.getGoalsByUserId(user.getUserId()));
    }

    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestHeader("Authorization") String token, @RequestBody Goal goal) throws Exception {
        String email = jwtUtil.extractUsername(token.substring(7));
        User user = userService.findUserEntityByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
        return ResponseEntity.ok(goalService.createGoal(user.getUserId(), goal));
    }

    @PatchMapping("/{goalId}/progress")
    public ResponseEntity<Goal> updateProgress(@PathVariable Long goalId, @RequestParam double amount) {
        return ResponseEntity.ok(goalService.updateGoalProgress(goalId, amount));
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }
}
