//file for password verification
package com.deadline.analyzer.service;

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

    /**
     * Register a new user
     * Note: Personality type is NOT assigned at registration
     * It will be determined after the user completes their first task
     */
    public User registerUser(String fullName, String email, String password) {
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreatedAt(System.currentTimeMillis());
        user.setUpdatedAt(System.currentTimeMillis());

        // Initialize default values (NO personality type yet)
        user.setAverageDup(0.0);
        user.setCompletionRate(0);
        user.setPersonalityType(null); // Will be assigned after first task analysis

        return userRepository.save(user);
    }

    /**
     * Login user with email and password
     * Returns user if credentials are valid
     */
    public Optional<User> loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }

        return Optional.empty();
    }

    /**
     * Get user by ID
     */
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Update user information
     */
    public User updateUser(User user) {
        user.setUpdatedAt(System.currentTimeMillis());
        return userRepository.save(user);
    }

    /**
     * Assign personality type after first task completion
     * Called by TaskService after analyzing first task
     *
     * Personality Types:
     * 1. Panic Starter - High DUP (70%+), Low success rate
     * 2. Perfectionist Delayer - Low DUP (20-40%), High edits
     * 3. Chronic Postponer - High DUP (70%+), Always late
     * 4. Last-Minute Sprinter - High DUP (70%+), High success rate
     * 5. Steady Planner - Medium DUP (20-50%), On-time (Default)
     */
    public User assignPersonalityType(String userId, String personalityType) {
        Optional<User> userOptional = getUserById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPersonalityType(personalityType);
            user.setUpdatedAt(System.currentTimeMillis());
            return userRepository.save(user);
        }

        return null;
    }

    /**
     * Update user's DUP average after task completion
     */
    public User updateAverageDup(String userId, Double newDup) {
        Optional<User> userOptional = getUserById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAverageDup(newDup);
            user.setUpdatedAt(System.currentTimeMillis());
            return userRepository.save(user);
        }

        return null;
    }

    /**
     * Update user's completion rate after task completion
     */
    public User updateCompletionRate(String userId, Integer completionRate) {
        Optional<User> userOptional = getUserById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setCompletionRate(completionRate);
            user.setUpdatedAt(System.currentTimeMillis());
            return userRepository.save(user);
        }

        return null;
    }

    /**
     * Delete user account
     */
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    /**
     * Verify if password matches the encrypted password
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}