package com.deadline.analyzer.service;

import com.deadline.analyzer.dto.request.CreateTaskRequest;
import com.deadline.analyzer.dto.response.TaskResponse;
import java.util.List;

public interface TaskService {
    TaskResponse createTask(String userId, CreateTaskRequest request) throws Exception;
    List<TaskResponse> getUserTasks(String userId);
    TaskResponse getTask(String taskId);
    void startTask(String taskId) throws Exception;
    void completeTask(String taskId) throws Exception;
    void deleteTask(String taskId) throws Exception;
}