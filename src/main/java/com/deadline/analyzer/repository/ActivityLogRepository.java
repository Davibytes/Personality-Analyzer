package com.deadline.analyzer.repository;

import com.deadline.analyzer.model.ActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {
    
    List<ActivityLog> findByUserEmailOrderByTimestampDesc(String userEmail);
    
    List<ActivityLog> findByActionOrderByTimestampDesc(String action);
    
    List<ActivityLog> findByTimestampBetweenOrderByTimestampDesc(Long startTime, Long endTime);
    
    List<ActivityLog> findTop100ByOrderByTimestampDesc();
}
