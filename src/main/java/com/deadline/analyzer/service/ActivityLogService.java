package com.deadline.analyzer.service;

import com.deadline.analyzer.model.ActivityLog;
import com.deadline.analyzer.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    /**
     * Log a user action with all details
     */
    public ActivityLog logActivity(String userId, String email, String actionType,
                                   Long formStartTime, Long formEndTime,
                                   Long dbSaveStartTime, Long dbSaveEndTime,
                                   HttpServletRequest request) {

        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setEmail(email);
        log.setActionType(actionType);
        log.setTimestamp(System.currentTimeMillis());

        // Form timing
        if (formStartTime != null && formEndTime != null) {
            long totalFormTime = formEndTime - formStartTime;
            log.setFormStartTime(formStartTime);
            log.setFormEndTime(formEndTime);
            log.setTotalFormTime(totalFormTime);
        }

        // Database save timing
        if (dbSaveStartTime != null && dbSaveEndTime != null) {
            long dbSaveTime = dbSaveEndTime - dbSaveStartTime;
            log.setDatabaseSaveTime(dbSaveTime);
        }

        // Set action description
        switch (actionType) {
            case "REGISTER":
                log.setAction("User registered new account");
                break;
            case "LOGIN":
                log.setAction("User logged in");
                break;
            case "CREATE_TASK":
                log.setAction("User created a task");
                break;
            case "COMPLETE_TASK":
                log.setAction("User completed a task");
                break;
            case "UPDATE_PROFILE":
                log.setAction("User updated profile");
                break;
            default:
                log.setAction("User performed action: " + actionType);
        }

        // Get IP and User Agent
        if (request != null) {
            log.setIpAddress(getClientIp(request));
            log.setUserAgent(request.getHeader("User-Agent"));
        }

        log.setStatus("SUCCESS");
        log.setAttemptNumber(1);

        return activityLogRepository.save(log);
    }

    /**
     * Log failed action
     */
    public ActivityLog logFailedActivity(String email, String actionType,
                                         String errorMessage, HttpServletRequest request) {

        ActivityLog log = new ActivityLog();
        log.setEmail(email);
        log.setActionType(actionType);
        log.setTimestamp(System.currentTimeMillis());
        log.setStatus("FAILED");
        log.setErrorMessage(errorMessage);
        log.setAction("Failed attempt: " + actionType);

        if (request != null) {
            log.setIpAddress(getClientIp(request));
            log.setUserAgent(request.getHeader("User-Agent"));
        }

        return activityLogRepository.save(log);
    }

    /**
     * Get all logs for a specific user
     */
    public List<ActivityLog> getUserActivityLogs(String userId) {
        return activityLogRepository.findByUserId(userId);
    }

    /**
     * Get all logs for a specific email
     */
    public List<ActivityLog> getEmailActivityLogs(String email) {
        return activityLogRepository.findByEmail(email);
    }

    /**
     * Get all logs of a specific action type
     */
    public List<ActivityLog> getActivityLogsByType(String actionType) {
        return activityLogRepository.findByActionType(actionType);
    }

    /**
     * Get all failed login/register attempts
     */
    public List<ActivityLog> getFailedAttempts() {
        return activityLogRepository.findByStatus("FAILED");
    }

    /**
     * Get logs within a time range (useful for analytics)
     */
    public List<ActivityLog> getActivityLogsByTimeRange(Long startTime, Long endTime) {
        return activityLogRepository.findByTimestampBetween(startTime, endTime);
    }

    /**
     * Get client IP address
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor == null || xForwardedFor.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xForwardedFor.split(",")[0];
    }
}