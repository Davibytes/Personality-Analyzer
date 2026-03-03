package com.deadline.analyzer.service;

import com.deadline.analyzer.dto.response.DashboardResponse;

public interface PersonalityService {
    DashboardResponse getUserDashboard(String userId) throws Exception;
}