package com.deadline.analyzer.controller;

import com.deadline.analyzer.aspect.TrackPerformance;
import com.deadline.analyzer.model.User;
import com.deadline.analyzer.service.ActivityLogService;
import com.deadline.analyzer.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final UserService userService;
    private final ActivityLogService activityLogService;

    /**
     * Example endpoint with performance tracking
     */
    @PostMapping("/process-data")
    @TrackPerformance(action = "Data Processing")
    public ResponseEntity<Map<String, Object>> processData(@RequestBody Map<String, Object> data, HttpServletRequest request) {
        
        try {
            // Simulate some processing time
            Thread.sleep(100);
            
            // Log the activity
            activityLogService.logActivity(
                "test@example.com",
                "Data Processing",
                "Processed " + data.size() + " fields",
                null,
                null,
                request
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Data processed successfully",
                "processedFields", data.size()
            ));
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Processing interrupted"
            ));
        }
    }

    /**
     * Simple activity logging example
     */
    @PostMapping("/log-action")
    public ResponseEntity<Map<String, Object>> logAction(@RequestBody Map<String, String> request) {
        
        String userEmail = request.get("userEmail");
        String action = request.get("action");
        String details = request.get("details");
        
        activityLogService.logActivity(userEmail, action, details);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Action logged successfully"
        ));
    }
}
