package com.deadline.analyzer.controller;

import com.deadline.analyzer.model.User;
import com.deadline.analyzer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<User> user = userService.getUserById(userId);

            if (user.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }

            User userData = user.get();
            System.out.println("User fetched: " + userData.getEmail() +
                    ", Personality: " + userData.getPersonalityType() +
                    ", AvgDUP: " + userData.getAverageDup() +
                    ", CompRate: " + userData.getCompletionRate());

            response.put("success", true);
            response.put("user", userData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error fetching user");
            return ResponseEntity.status(500).body(response);
        }
    }
}