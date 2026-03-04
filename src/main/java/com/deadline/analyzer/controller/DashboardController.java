package com.deadline.analyzer.controller;

import com.deadline.analyzer.dto.response.ApiResponse;
import com.deadline.analyzer.dto.response.DashboardResponse;
import com.deadline.analyzer.service.PersonalityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final PersonalityService personalityService;

    public DashboardController(PersonalityService personalityService) {
        this.personalityService = personalityService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getDashboard(Authentication authentication) {
        try {
            String userId = authentication.getName();
            DashboardResponse dashboard = personalityService.getUserDashboard(userId);
            return ResponseEntity.ok(ApiResponse.success("Dashboard fetched", dashboard));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}