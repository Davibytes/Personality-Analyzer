package com.deadline.analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.google.cloud.firestore.annotation.DocumentId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalityProfile {

    @DocumentId
    private String profileId;
    private String userId;
    private String personalityType;
    private long classificationDate;
    private double averageDup;
    private double completionRate;
    private int totalTasksCompleted;
    private int lateSubmissions;

    public PersonalityProfile(String userId, String personalityType, long classificationDate) {
        this.userId = userId;
        this.personalityType = personalityType;
        this.classificationDate = classificationDate;
    }
}