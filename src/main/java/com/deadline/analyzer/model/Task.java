package com.deadline.analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
public class Task {
    @Id
    private String id;
    private String userId;
    private String title;
    private String description;
    private Long createdAt;
    private Long deadline;
    private Long startedAt;
    private Long completedAt;
    private Boolean isCompleted;
    private Boolean isLate;
    private Double dupScore;
}