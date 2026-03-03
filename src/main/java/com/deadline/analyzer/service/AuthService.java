package com.deadline.analyzer.service;

import com.deadline.analyzer.dto.request.LoginRequest;
import com.deadline.analyzer.dto.request.SignupRequest;
import com.deadline.analyzer.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request) throws Exception;
    AuthResponse signup(SignupRequest request) throws Exception;
    AuthResponse refreshToken(String token) throws Exception;
}