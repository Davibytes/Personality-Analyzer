package com.deadline.analyzer.service.impl;

import com.deadline.analyzer.dto.request.LoginRequest;
import com.deadline.analyzer.dto.request.SignupRequest;
import com.deadline.analyzer.dto.response.AuthResponse;
import com.deadline.analyzer.exception.ValidationException;
import com.deadline.analyzer.model.User;
import com.deadline.analyzer.repository.UserRepository;
import com.deadline.analyzer.security.JwtTokenProvider;
import com.deadline.analyzer.util.Constants;
import com.deadline.analyzer.util.DateUtils;
import com.deadline.analyzer.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(LoginRequest request) throws Exception {
        if (!ValidationUtils.isValidEmail(request.getEmail())) {
            throw new ValidationException(Constants.ValidationMessages.INVALID_EMAIL);
        }

        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new ValidationException("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ValidationException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getName())
                .personalityType(user.getPersonalityType())
                .build();
    }

    @Override
    public AuthResponse signup(SignupRequest request) throws Exception {
        if (!ValidationUtils.isValidEmail(request.getEmail())) {
            throw new ValidationException(Constants.ValidationMessages.INVALID_EMAIL);
        }

        if (!ValidationUtils.isValidPassword(request.getPassword())) {
            throw new ValidationException(Constants.ValidationMessages.WEAK_PASSWORD);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException(Constants.ValidationMessages.PASSWORDS_MISMATCH);
        }

        if (!ValidationUtils.isNotEmpty(request.getFullName())) {
            throw new ValidationException(Constants.ValidationMessages.EMPTY_FIELD);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException(Constants.ValidationMessages.USER_EXISTS);
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());
        String userId = UUID.randomUUID().toString();

        User user = new User(
                request.getFullName(),
                request.getEmail(),
                passwordHash,
                DateUtils.getCurrentTimestampUTC()
        );
        user.setUserId(userId);

        user = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getName())
                .personalityType(user.getPersonalityType())
                .build();
    }

    @Override
    public AuthResponse refreshToken(String token) throws Exception {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new ValidationException("Invalid token");
        }

        String userId = jwtTokenProvider.getUserIdFromToken(token);
        User user = userRepository.findById(userId);

        if (user == null) {
            throw new ValidationException("User not found");
        }

        String newToken = jwtTokenProvider.generateToken(user.getUserId(), user.getEmail());

        return AuthResponse.builder()
                .token(newToken)
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getName())
                .personalityType(user.getPersonalityType())
                .build();
    }
}