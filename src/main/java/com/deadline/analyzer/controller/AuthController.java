package com.deadline.analyzer.controller;

import com.deadline.analyzer.dto.AuthResponse;
import com.deadline.analyzer.dto.LoginRequest;
import com.deadline.analyzer.dto.RegisterRequest;
import com.deadline.analyzer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final UserService userService;

    /**
     * Register a new user
     * POST /api/auth/register
     * @param request RegisterRequest with fullName, email, password, confirmPassword
     * @return AuthResponse with user details and success status
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        // Validate request
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            AuthResponse response = new AuthResponse();
            response.setSuccess(false);
            response.setMessage("Full name is required");
            return ResponseEntity.badRequest().body(response);
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            AuthResponse response = new AuthResponse();
            response.setSuccess(false);
            response.setMessage("Email is required");
            return ResponseEntity.badRequest().body(response);
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            AuthResponse response = new AuthResponse();
            response.setSuccess(false);
            response.setMessage("Password is required");
            return ResponseEntity.badRequest().body(response);
        }

        AuthResponse response = userService.registerUser(request);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Login user
     * POST /api/auth/login
     * @param request LoginRequest with email and password
     * @return AuthResponse with user details and success status
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Validate request
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            AuthResponse response = new AuthResponse();
            response.setSuccess(false);
            response.setMessage("Email is required");
            return ResponseEntity.badRequest().body(response);
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            AuthResponse response = new AuthResponse();
            response.setSuccess(false);
            response.setMessage("Password is required");
            return ResponseEntity.badRequest().body(response);
        }

        AuthResponse response = userService.loginUser(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}

