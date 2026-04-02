package com.deadline.analyzer.service;

import com.deadline.analyzer.dto.PerformanceData;
import com.deadline.analyzer.model.ActivityLog;
import com.deadline.analyzer.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    /**
     * Enhanced activity logging with start/end times
     */
    public void logActivity(String userEmail, String action, String details, 
                           Long inputTimeMs, Long processingTimeMs,
                           Long startTime, Long endTime,
                           HttpServletRequest request) {
        
        long currentTime = System.currentTimeMillis();
        String readableTimestamp = java.time.Instant.ofEpochMilli(currentTime)
            .atZone(java.time.ZoneId.systemDefault())
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // Create log entry
        ActivityLog activityLog = new ActivityLog();
        activityLog.setUserEmail(userEmail);
        activityLog.setAction(action);
        activityLog.setDetails(details);
        activityLog.setInputTimeMs(inputTimeMs);
        activityLog.setProcessingTimeMs(processingTimeMs);
        activityLog.setStartTime(startTime);
        activityLog.setEndTime(endTime);
        activityLog.setTimestamp(currentTime);
        
        if (request != null) {
            activityLog.setUserAgent(request.getHeader("User-Agent"));
            activityLog.setIpAddress(getClientIpAddress(request));
        }

        // Enhanced console logging with readable timestamp
        String logMessage = String.format(
            "[%s] USER: %s | ACTION: %s | DETAILS: %s | START: %s | END: %s | DURATION: %dms | PROCESSING: %dms | IP: %s",
            readableTimestamp,
            activityLog.getUserEmail(),
            activityLog.getAction(),
            activityLog.getDetails(),
            formatTimestamp(startTime),
            formatTimestamp(endTime),
            inputTimeMs,
            activityLog.getProcessingTimeMs(),
            activityLog.getIpAddress()
        );
        
        log.info("ACTIVITY_LOG: {}", logMessage);

        // Save to database
        try {
            activityLogRepository.save(activityLog);
        } catch (Exception e) {
            log.error("Failed to save activity log to database: {}", e.getMessage());
        }
    }

    /**
     * Format timestamp for logging
     */
    private String formatTimestamp(Long timestamp) {
        if (timestamp == null) return "N/A";
        return java.time.Instant.ofEpochMilli(timestamp)
            .atZone(java.time.ZoneId.systemDefault())
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Simple activity logging (no performance tracking)
     */
    public void logActivity(String userEmail, String action, String details) {
        logActivity(userEmail, action, details, null, null, null, null, null);
    }

    /**
     * Log performance data with start/end times
     */
    public void logPerformanceData(PerformanceData performanceData, HttpServletRequest request) {
        logActivity(
            performanceData.getUserEmail(),
            performanceData.getAction(),
            performanceData.getDetails(),
            performanceData.getInputTimeMs(),
            null, // processing time will be tracked by aspect
            performanceData.getStartTime(),
            performanceData.getEndTime(),
            request
        );
    }

    /**
     * Log to file using SLF4J
     */
    private void logToFile(ActivityLog activityLog) {
        String logMessage = String.format(
            "USER: %s | ACTION: %s | DETAILS: %s | INPUT_TIME: %dms | PROCESSING_TIME: %dms | IP: %s | TIMESTAMP: %d",
            activityLog.getUserEmail(),
            activityLog.getAction(),
            activityLog.getDetails(),
            activityLog.getInputTimeMs(),
            activityLog.getProcessingTimeMs(),
            activityLog.getIpAddress(),
            activityLog.getTimestamp()
        );

        if (activityLog.getInputTimeMs() != null && activityLog.getProcessingTimeMs() != null) {
            log.info("PERFORMANCE_LOG: {}", logMessage);
        } else {
            log.info("ACTIVITY_LOG: {}", logMessage);
        }
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
