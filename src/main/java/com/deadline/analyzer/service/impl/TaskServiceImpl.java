package com.deadline.analyzer.service.impl;

import com.deadline.analyzer.dto.request.CreateTaskRequest;
import com.deadline.analyzer.dto.response.TaskResponse;
import com.deadline.analyzer.exception.NotFoundException;
import com.deadline.analyzer.exception.ValidationException;
import com.deadline.analyzer.model.Task;
import com.deadline.analyzer.repository.TaskRepository;
import com.deadline.analyzer.util.Constants;
import com.deadline.analyzer.util.DateUtils;
import com.deadline.analyzer.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public TaskResponse createTask(String userId, CreateTaskRequest request) throws Exception {
        if (!ValidationUtils.isValidTaskTitle(request.getTitle())) {
            throw new ValidationException("Task title must be between 3-200 characters");
        }

        if (!ValidationUtils.isValidDeadline(request.getDeadline())) {
            throw new ValidationException("Deadline must be in the future");
        }

        Task task = new Task(
                userId,
                request.getTitle(),
                request.getDescription(),
                DateUtils.getCurrentTimestampUTC(),
                request.getDeadline()
        );

        task = taskRepository.save(task);

        return TaskResponse.fromTask(task);
    }

    @Override
    public List<TaskResponse> getUserTasks(String userId) {
        List<Task> tasks = taskRepository.findByUserId(userId);
        return tasks.stream()
                .map(TaskResponse::fromTask)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse getTask(String taskId) {
        Task task = taskRepository.findById(taskId);
        if (task == null) {
            throw new NotFoundException("Task not found");
        }
        return TaskResponse.fromTask(task);
    }

    @Override
    public void startTask(String taskId) throws Exception {
        Task task = taskRepository.findById(taskId);
        if (task == null) {
            throw new NotFoundException("Task not found");
        }

        long startTime = DateUtils.getCurrentTimestampUTC();
        double dup = DateUtils.calculateDUP(task.getCreationTime(), startTime, task.getDeadline());

        task.setStartTime(startTime);
        task.setDup(dup);
        task.setStatus(Constants.TaskStatus.IN_PROGRESS);

        taskRepository.update(task);
    }

    @Override
    public void completeTask(String taskId) throws Exception {
        Task task = taskRepository.findById(taskId);
        if (task == null) {
            throw new NotFoundException("Task not found");
        }

        long completionTime = DateUtils.getCurrentTimestampUTC();
        boolean isLate = completionTime > task.getDeadline();

        task.setCompletionTime(completionTime);
        task.setStatus(Constants.TaskStatus.COMPLETED);
        task.setLateSubmission(isLate);

        taskRepository.update(task);
    }

    @Override
    public void deleteTask(String taskId) throws Exception {
        Task task = taskRepository.findById(taskId);
        if (task == null) {
            throw new NotFoundException("Task not found");
        }
        taskRepository.delete(taskId);
    }
}