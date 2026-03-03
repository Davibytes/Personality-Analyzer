package com.deadline.analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    private double averageDup;
    private double completionRate;
    private String personalityType;
    private String recommendation;
    private int totalTasksCompleted;
    private int lateSubmissions;
    private List<TaskResponse> recentTasks;
}