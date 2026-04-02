package com.deadline.analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceData {
    
    private String userEmail;
    private String action;
    private Long inputTimeMs;  // Time user spent filling form
    private Long startTime;  // When user started action
    private Long endTime;  // When user completed action
    private String details;
    
}
