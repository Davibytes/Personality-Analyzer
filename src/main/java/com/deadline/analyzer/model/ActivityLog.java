package com.deadline.analyzer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "activityLogs")
public class ActivityLog {

    @Id
    private String id;
    private String userId;
    private String email;
    private String action;
    private String actionType; // LOGIN, REGISTER, CREATE_TASK, COMPLETE_TASK, etc
    private Long timestamp;
    private Long actionDuration; // How long the action took in milliseconds
    private String ipAddress;
    private String userAgent;
    private Long formStartTime; // When user started filling form
    private Long formEndTime; // When user submitted form
    private Long totalFormTime; // Total time from start to submission in milliseconds
    private Long databaseSaveTime; // Time it took to save to database
    private Integer attemptNumber; // Attempt 1, 2, 3, etc
    private String status; // SUCCESS, FAILED
    private String errorMessage; // If failed, why

    // Constructors
    public ActivityLog() {}

    public ActivityLog(String userId, String email, String action, String actionType) {
        this.userId = userId;
        this.email = email;
        this.action = action;
        this.actionType = actionType;
        this.timestamp = System.currentTimeMillis();
        this.status = "SUCCESS";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getActionDuration() {
        return actionDuration;
    }

    public void setActionDuration(Long actionDuration) {
        this.actionDuration = actionDuration;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Long getFormStartTime() {
        return formStartTime;
    }

    public void setFormStartTime(Long formStartTime) {
        this.formStartTime = formStartTime;
    }

    public Long getFormEndTime() {
        return formEndTime;
    }

    public void setFormEndTime(Long formEndTime) {
        this.formEndTime = formEndTime;
    }

    public Long getTotalFormTime() {
        return totalFormTime;
    }

    public void setTotalFormTime(Long totalFormTime) {
        this.totalFormTime = totalFormTime;
    }

    public Long getDatabaseSaveTime() {
        return databaseSaveTime;
    }

    public void setDatabaseSaveTime(Long databaseSaveTime) {
        this.databaseSaveTime = databaseSaveTime;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}