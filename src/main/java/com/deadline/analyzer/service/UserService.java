package com.deadline.analyzer.service;

import com.deadline.analyzer.dto.AuthResponse;
import com.deadline.analyzer.dto.LoginRequest;
import com.deadline.analyzer.dto.RegisterRequest;
import com.deadline.analyzer.model.User;
import com.deadline.analyzer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User createUser(User user) {
        user.setCreatedAt(System.currentTimeMillis());
        user.setUpdatedAt(System.currentTimeMillis());
        return userRepository.save(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public User updateUser(User user) {
        user.setUpdatedAt(System.currentTimeMillis());
        return userRepository.save(user);
    }

    /**
     * Register a new user
     * @param request RegisterRequest containing user details
     * @return AuthResponse with success/failure status
     */
    public AuthResponse registerUser(RegisterRequest request) {
        AuthResponse response = new AuthResponse();

        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            response.setSuccess(false);
            response.setMessage("Passwords do not match");
            return response;
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            response.setSuccess(false);
            response.setMessage("Email already registered");
            return response;
        }

        // Validate email format
        if (!isValidEmail(request.getEmail())) {
            response.setSuccess(false);
            response.setMessage("Invalid email format");
            return response;
        }

        // Validate password strength
        if (request.getPassword().length() < 6) {
            response.setSuccess(false);
            response.setMessage("Password must be at least 6 characters long");
            return response;
        }

        try {
            // Create new user
            User newUser = new User();
            newUser.setFullName(request.getFullName());
            newUser.setEmail(request.getEmail());
            // Hash password using BCrypt
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setCreatedAt(System.currentTimeMillis());
            newUser.setUpdatedAt(System.currentTimeMillis());

            // Set default values for personality analysis fields
            newUser.setPersonalityType("UNANALYZED");
            newUser.setAverageDup(0.0);
            newUser.setCompletionRate(0);

            // Save user to MongoDB
            User savedUser = userRepository.save(newUser);

            response.setSuccess(true);
            response.setMessage("User registered successfully");
            response.setUserId(savedUser.getId());
            response.setFullName(savedUser.getFullName());
            response.setEmail(savedUser.getEmail());

            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("An error occurred during registration: " + e.getMessage());
            return response;
        }
    }

    /**
     * Authenticate user login
     * @param request LoginRequest containing email and password
     * @return AuthResponse with success/failure status
     */
    public AuthResponse loginUser(LoginRequest request) {
        AuthResponse response = new AuthResponse();

        try {
            // Find user by email
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

            if (userOptional.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Invalid email or password");
                return response;
            }

            User user = userOptional.get();

            // Verify password using BCrypt
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                response.setSuccess(false);
                response.setMessage("Invalid email or password");
                return response;
            }

            // Update last login time
            user.setUpdatedAt(System.currentTimeMillis());
            userRepository.save(user);

            // Return success response
            response.setSuccess(true);
            response.setMessage("Login successful");
            response.setUserId(user.getId());
            response.setFullName(user.getFullName());
            response.setEmail(user.getEmail());

            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("An error occurred during login: " + e.getMessage());
            return response;
        }
    }

    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
}