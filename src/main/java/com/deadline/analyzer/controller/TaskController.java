package com.deadline.analyzer.controller;

import com.deadline.analyzer.dto.request.CreateTaskRequest;
import com.deadline.analyzer.dto.response.ApiResponse;
import com.deadline.analyzer.dto.response.TaskResponse;
import com.deadline.analyzer.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<ApiResponse> getUserTasks(Authentication authentication) {
        try {
            String userId = authentication.getName();
            List<TaskResponse> tasks = taskService.getUserTasks(userId);
            return ResponseEntity.ok(ApiResponse.success("Tasks fetched", tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            TaskResponse task = taskService.createTask(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Task created", task));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse> getTask(@PathVariable String taskId) {
        try {
            TaskResponse task = taskService.getTask(taskId);
            return ResponseEntity.ok(ApiResponse.success("Task fetched", task));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{taskId}/start")
    public ResponseEntity<ApiResponse> startTask(@PathVariable String taskId) {
        try {
            taskService.startTask(taskId);
            return ResponseEntity.ok(ApiResponse.success("Task started", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{taskId}/complete")
    public ResponseEntity<ApiResponse> completeTask(@PathVariable String taskId) {
        try {
            taskService.completeTask(taskId);
            return ResponseEntity.ok(ApiResponse.success("Task completed", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable String taskId) {
        try {
            taskService.deleteTask(taskId);
            return ResponseEntity.ok(ApiResponse.success("Task deleted", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}