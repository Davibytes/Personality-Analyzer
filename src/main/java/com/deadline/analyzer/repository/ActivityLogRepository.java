package com.deadline.analyzer.repository;

import com.deadline.analyzer.model.ActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {

    // Find all logs for a specific user
    List<ActivityLog> findByUserId(String userId);

    // Find all logs for a specific email
    List<ActivityLog> findByEmail(String email);

    // Find all logs of a specific action type
    List<ActivityLog> findByActionType(String actionType);

    // Find all failed attempts
    List<ActivityLog> findByStatus(String status);

    // Find logs within a time range
    List<ActivityLog> findByTimestampBetween(Long startTime, Long endTime);
}