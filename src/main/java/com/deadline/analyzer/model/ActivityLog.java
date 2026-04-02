package com.deadline.analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "activity_logs")
public class ActivityLog {
    @Id
    private String id;
    private String userEmail;
    private String action;
    private String details;
    private Long inputTimeMs;  // Time user spent filling form
    private Long processingTimeMs;  // Time backend took to process
    private Long startTime;  // When user started action
    private Long endTime;  // When user completed action
    private Long timestamp;  // Log creation time
    private String userAgent;
    private String ipAddress;
}
