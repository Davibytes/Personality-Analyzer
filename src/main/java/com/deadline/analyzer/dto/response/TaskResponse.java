package com.deadline.analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.deadline.analyzer.model.Task;
import com.deadline.analyzer.util.DateUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    private String taskId;
    private String title;
    private String description;
    private String creationDate;
    private String deadline;
    private String status;
    private double dup;
    private boolean isLateSubmission;
    private String timeRemaining;

    public static TaskResponse fromTask(Task task) {
        return TaskResponse.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .creationDate(DateUtils.formatDate(task.getCreationTime()))
                .deadline(DateUtils.formatDate(task.getDeadline()))
                .status(task.getStatus())
                .dup(Math.round(task.getDup() * 100.0) / 100.0)
                .isLateSubmission(task.isLateSubmission())
                .timeRemaining(DateUtils.formatTimeRemaining(task.getDeadline()))
                .build();
    }
}