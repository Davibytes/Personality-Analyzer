package com.deadline.analyzer.service;

import com.deadline.analyzer.model.Task;
import com.deadline.analyzer.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }

    public Task completeTask(String id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setCompletedAt(System.currentTimeMillis());
            task.setIsCompleted(true);

            if (task.getCompletedAt() > task.getDeadline()) {
                task.setIsLate(true);
            }

            long timeAvailable = task.getDeadline() - task.getCreatedAt();
            long timeWaited = task.getStartedAt() - task.getCreatedAt();
            double dup = (timeWaited / (double) timeAvailable) * 100;
            task.setDupScore(dup);

            return taskRepository.save(task);
        }
        return null;
    }
}