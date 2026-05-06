package com.deadline.analyzer.controller;

import com.deadline.analyzer.model.Task;
import com.deadline.analyzer.model.User;
import com.deadline.analyzer.service.TaskService;
import com.deadline.analyzer.service.UserService;
import com.deadline.analyzer.service.ActivityLogService;
import com.deadline.analyzer.service.PersonalityAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final ActivityLogService activityLogService;
    private final PersonalityAnalysisService personalityAnalysisService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createTask(@RequestBody Map<String, Object> request,
                                                          HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        long dbStartTime = System.currentTimeMillis();

        try {
            String userId = (String) request.get("userId");
            String title = (String) request.get("title");
            String description = (String) request.get("description");
            Long deadline = ((Number) request.get("deadline")).longValue();
            String priority = (String) request.get("priority");

            Optional<User> user = userService.getUserById(userId);
            if (user.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }

            Task task = new Task();
            task.setUserId(userId);
            task.setTitle(title);
            task.setDescription(description);
            task.setDeadline(deadline);
            task.setPriority(priority);
            task.setCreatedAt(System.currentTimeMillis());
            task.setIsCompleted(false);
            task.setIsLate(false);

            Task savedTask = taskService.createTask(task);
            long dbEndTime = System.currentTimeMillis();

            activityLogService.logActivity(userId, user.get().getEmail(), "CREATE_TASK",
                    null, null, dbStartTime, dbEndTime, httpRequest);

            response.put("success", true);
            response.put("message", "Task created successfully");
            response.put("taskId", savedTask.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating task: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserTasks(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Task> tasks = taskService.getUserTasks(userId);
            response.put("success", true);
            response.put("tasks", tasks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error loading tasks");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{taskId}/start")
    public ResponseEntity<Map<String, Object>> startTask(@PathVariable String taskId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Task> taskOptional = taskService.getTaskById(taskId);
            if (taskOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "Task not found");
                return ResponseEntity.badRequest().body(response);
            }

            Task task = taskOptional.get();
            task.setStartedAt(System.currentTimeMillis());
            taskService.updateTask(task);

            response.put("success", true);
            response.put("message", "Task started");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error starting task");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{taskId}/complete")
    public ResponseEntity<Map<String, Object>> completeTask(@PathVariable String taskId,
                                                            HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Task> taskOptional = taskService.getTaskById(taskId);
            if (taskOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "Task not found");
                return ResponseEntity.badRequest().body(response);
            }

            Task task = taskOptional.get();
            long completionTime = System.currentTimeMillis();
            task.setCompletedAt(completionTime);
            task.setIsCompleted(true);
            task.setUpdatedAt(completionTime);

            // Check if late (completed after deadline)
            boolean isLate = completionTime > task.getDeadline();
            task.setIsLate(isLate);

            // Calculate DUP Score (Deadline Utilization Percentage)
            double dupScore = 0.0;
            if (task.getStartedAt() != null && task.getCreatedAt() != null) {
                long timeAvailable = task.getDeadline() - task.getCreatedAt();
                long timeWaited = task.getStartedAt() - task.getCreatedAt();

                if (timeAvailable > 0) {
                    dupScore = Math.min(100.0, (timeWaited / (double) timeAvailable) * 100);
                }
                task.setDupScore(dupScore);
            }

            // Save updated task
            Task updatedTask = taskService.updateTask(task);

            // Get user
            Optional<User> userOptional = userService.getUserById(task.getUserId());
            if (userOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }

            User user = userOptional.get();

            // Analyze personality based on all completed tasks
            PersonalityAnalysisService.PersonalityAnalysis analysis =
                    personalityAnalysisService.analyzeUserPersonality(task.getUserId());

            if (analysis != null) {
                // Update user with new analysis
                user.setAverageDup(Math.round(analysis.getAverageDup() * 100.0) / 100.0);
                user.setCompletionRate(analysis.getCompletionRate());
                user.setPersonalityType(analysis.getPersonalityType());
                user.setUpdatedAt(System.currentTimeMillis());
                userService.updateUser(user);

                // Log the activity
                activityLogService.logActivity(task.getUserId(), user.getEmail(),
                        "COMPLETE_TASK", null, null, null, null, httpRequest);

                response.put("success", true);
                response.put("message", "Task completed successfully!");
                response.put("task", updatedTask);
                response.put("analysis", new HashMap<String, Object>() {{
                    put("personalityType", analysis.getPersonalityType());
                    put("averageDup", analysis.getAverageDup());
                    put("completionRate", analysis.getCompletionRate());
                    put("onTimeSubmissions", analysis.getOnTimeSubmissions());
                    put("lateSubmissions", analysis.getLateSubmissions());
                    put("recommendation", analysis.getRecommendation());
                }});

                return ResponseEntity.ok(response);
            } else {
                response.put("success", true);
                response.put("message", "Task completed");
                response.put("task", updatedTask);
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error completing task: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable String taskId) {
        Map<String, Object> response = new HashMap<>();

        try {
            taskService.deleteTask(taskId);
            response.put("success", true);
            response.put("message", "Task deleted");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting task");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}