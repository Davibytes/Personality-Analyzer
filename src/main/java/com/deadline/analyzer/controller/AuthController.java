package com.deadline.analyzer.controller;

import com.deadline.analyzer.model.User;
import com.deadline.analyzer.service.UserService;
import com.deadline.analyzer.service.RecaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final RecaptchaService recaptchaService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String fullName = request.get("fullName");
            String email = request.get("email");
            String password = request.get("password");
            String recaptchaToken = request.get("recaptchaToken");

            // Verify reCAPTCHA first
            if (recaptchaToken == null || recaptchaToken.isEmpty()) {
                response.put("success", false);
                response.put("message", "reCAPTCHA token is missing");
                return ResponseEntity.badRequest().body(response);
            }

            if (!recaptchaService.verifyRecaptcha(recaptchaToken)) {
                response.put("success", false);
                response.put("message", "reCAPTCHA verification failed. Please try again.");
                return ResponseEntity.badRequest().body(response);
            }

            // Validation
            if (fullName == null || fullName.trim().isEmpty()) {
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
                response.put("success", false);
                response.put("message", "Password must be at least 6 characters");
                return ResponseEntity.badRequest().body(response);
            }

            // Register user
            User newUser = userService.registerUser(fullName, email, password);

            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("userId", newUser.getId());
            response.put("email", newUser.getEmail());
            response.put("fullName", newUser.getFullName());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = request.get("email");
            String password = request.get("password");
            String recaptchaToken = request.get("recaptchaToken");

            // Verify reCAPTCHA first
            if (recaptchaToken == null || recaptchaToken.isEmpty()) {
                response.put("success", false);
                response.put("message", "reCAPTCHA token is missing");
                return ResponseEntity.badRequest().body(response);
            }

            if (!recaptchaService.verifyRecaptcha(recaptchaToken)) {
                response.put("success", false);
                response.put("message", "reCAPTCHA verification failed. Please try again.");
                return ResponseEntity.badRequest().body(response);
            }

            // Validation
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
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            response.put("success", true);
            response.put("message", "Login successful");
            response.put("userId", user.get().getId());
            response.put("email", user.get().getEmail());
            response.put("fullName", user.get().getFullName());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}