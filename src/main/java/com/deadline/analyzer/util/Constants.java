package com.deadline.analyzer.util;

public class Constants {

    public static final class PersonalityTypes {
        public static final String PANIC_STARTER = "Panic Starter";
        public static final String PERFECTIONIST = "Perfectionist Delayer";
        public static final String CHRONIC_POSTPONER = "Chronic Postponer";
        public static final String LAST_MINUTE = "Last-Minute Sprinter";
        public static final String STEADY_PLANNER = "Steady Planner";
    }

    public static final class DUPThresholds {
        public static final double PANIC = 70.0;
        public static final double STEADY_MIN = 20.0;
        public static final double STEADY_MAX = 50.0;
    }

    public static final class TaskStatus {
        public static final String PENDING = "pending";
        public static final String IN_PROGRESS = "in_progress";
        public static final String COMPLETED = "completed";
        public static final String OVERDUE = "overdue";
    }

    public static final class FirestoreCollections {
        public static final String USERS = "users";
        public static final String TASKS = "tasks";
        public static final String PERSONALITY_PROFILES = "personality_profiles";
    }

    public static final class ValidationMessages {
        public static final String INVALID_EMAIL = "Invalid email address";
        public static final String WEAK_PASSWORD = "Password must be 8+ characters with 1 uppercase, 1 number";
        public static final String PASSWORDS_MISMATCH = "Passwords do not match";
        public static final String EMPTY_FIELD = "This field cannot be empty";
        public static final String USER_EXISTS = "User already exists with this email";
    }
}