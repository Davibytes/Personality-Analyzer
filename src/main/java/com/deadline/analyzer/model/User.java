package com.deadline.analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.google.cloud.firestore.annotation.DocumentId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @DocumentId
    private String userId;
    private String name;
    private String email;
    private String passwordHash;
    private long registrationDate;
    private String profileImageUrl;
    private String personalityType;

    public User(String name, String email, String passwordHash, long registrationDate) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.registrationDate = registrationDate;
    }
}