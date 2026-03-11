package com.deadline.analyzer.service;

import com.deadline.analyzer.model.Task;
import com.deadline.analyzer.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task createTask(Task task) {
        task.setCreatedAt(System.currentTimeMillis());
        task.setIsCompleted(false);
        task.setIsLate(false);
        return taskRepository.save(task);
    }

    public List<Task> getUserTasks(String userId) {
        return taskRepository.findByUserId(userId);
    }

    public Task getTaskById(String id) {
        return taskRepository.findById(id).orElse(null);
    }

    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }

    public Task completeTask(String id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setCompletedAt(System.currentTimeMillis());
            task.setIsCompleted(true);

            // Calculate if late
            if (task.getCompletedAt() > task.getDeadline()) {
                task.setIsLate(true);
            }

            // Calculate DUP
            long timeAvailable = task.getDeadline() - task.getCreatedAt();
            long timeWaited = task.getStartedAt() - task.getCreatedAt();
            double dup = (timeWaited / (double) timeAvailable) * 100;
            task.setDupScore(dup);

            return taskRepository.save(task);
        }
        return null;
    }
}
