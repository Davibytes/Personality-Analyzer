package com.deadline.analyzer.controller;

import com.deadline.analyzer.model.User;
import com.deadline.analyzer.service.UserService;
import com.deadline.analyzer.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final ActivityLogService activityLogService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request,
                                                        HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        long dbStartTime = System.currentTimeMillis();

        try {
            String fullName = request.get("fullName");
            String email = request.get("email");
            String password = request.get("password");
            String formStartTime = request.get("formStartTime");
            String formEndTime = request.get("formEndTime");

            // Validation
            if (fullName == null || fullName.trim().isEmpty()) {
                activityLogService.logFailedActivity(email, "REGISTER", "Full name is empty", httpRequest);
                response.put("success", false);
                response.put("message", "Full name is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Password is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (password.length() < 6) {
                activityLogService.logFailedActivity(email, "REGISTER", "Password too short", httpRequest);
                response.put("success", false);
                response.put("message", "Password must be at least 6 characters");
                return ResponseEntity.badRequest().body(response);
            }

            // Register user
            User newUser = userService.registerUser(fullName, email, password);
            long dbEndTime = System.currentTimeMillis();

            // Log successful registration
            Long formStart = formStartTime != null ? Long.parseLong(formStartTime) : null;
            Long formEnd = formEndTime != null ? Long.parseLong(formEndTime) : null;
            activityLogService.logActivity(newUser.getId(), newUser.getEmail(), "REGISTER",
                    formStart, formEnd, dbStartTime, dbEndTime, httpRequest);

            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("userId", newUser.getId());
            response.put("email", newUser.getEmail());
            response.put("fullName", newUser.getFullName());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            String email = request.get("email");
            activityLogService.logFailedActivity(email, "REGISTER", e.getMessage(), httpRequest);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            String email = request.get("email");
            activityLogService.logFailedActivity(email, "REGISTER", e.getMessage(), httpRequest);
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request,
                                                     HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        long dbStartTime = System.currentTimeMillis();

        try {
            String email = request.get("email");
            String password = request.get("password");
            String formStartTime = request.get("formStartTime");
            String formEndTime = request.get("formEndTime");

            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Password is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Login user
            Optional<User> user = userService.loginUser(email, password);

            if (user.isEmpty()) {
                activityLogService.logFailedActivity(email, "LOGIN", "Invalid credentials", httpRequest);
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            long dbEndTime = System.currentTimeMillis();

            // Log successful login
            Long formStart = formStartTime != null ? Long.parseLong(formStartTime) : null;
            Long formEnd = formEndTime != null ? Long.parseLong(formEndTime) : null;
            activityLogService.logActivity(user.get().getId(), user.get().getEmail(), "LOGIN",
                    formStart, formEnd, dbStartTime, dbEndTime, httpRequest);

            response.put("success", true);
            response.put("message", "Login successful");
            response.put("userId", user.get().getId());
            response.put("email", user.get().getEmail());
            response.put("fullName", user.get().getFullName());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String email = request.get("email");
            activityLogService.logFailedActivity(email, "LOGIN", e.getMessage(), httpRequest);
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}