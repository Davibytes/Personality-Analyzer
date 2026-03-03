package com.deadline.analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.google.cloud.firestore.annotation.DocumentId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @DocumentId
    private String taskId;
    private String userId;
    private String title;
    private String description;
    private long creationTime;
    private long startTime;
    private long completionTime;
    private long deadline;
    private int editCount;
    private int pauseCount;
    private long sessionDuration;
    private String status;
    private double dup;
    private boolean isLateSubmission;

    public Task(String userId, String title, String description,
                long creationTime, long deadline) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.creationTime = creationTime;
        this.deadline = deadline;
        this.status = "pending";
        this.editCount = 0;
        this.pauseCount = 0;
        this.dup = 0;
        this.isLateSubmission = false;
    }
}