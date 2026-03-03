package com.deadline.analyzer.service.impl;

import com.deadline.analyzer.dto.response.DashboardResponse;
import com.deadline.analyzer.dto.response.TaskResponse;
import com.deadline.analyzer.model.PersonalityProfile;
import com.deadline.analyzer.model.Task;
import com.deadline.analyzer.repository.PersonalityRepository;
import com.deadline.analyzer.repository.TaskRepository;
import com.deadline.analyzer.repository.UserRepository;
import com.deadline.analyzer.service.PersonalityService;
import com.deadline.analyzer.util.PersonalityClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonalityServiceImpl implements PersonalityService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PersonalityRepository personalityRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public DashboardResponse getUserDashboard(String userId) throws Exception {
        List<Task> tasks = taskRepository.findByUserId(userId);

        String personalityType = PersonalityClassifier.classifyPersonality(tasks);
        String recommendation = PersonalityClassifier.getRecommendation(personalityType);

        double avgDup = calculateAverageDUP(tasks);
        double completionRate = calculateCompletionRate(tasks);
        int totalCompleted = (int) tasks.stream()
                .filter(t -> "completed".equals(t.getStatus()))
                .count();
        int lateSubmissions = (int) tasks.stream()
                .filter(Task::isLateSubmission)
                .count();

        List<TaskResponse> recentTasks = tasks.stream()
                .limit(5)
                .map(TaskResponse::fromTask)
                .collect(Collectors.toList());

        if (!tasks.isEmpty()) {
            PersonalityProfile profile = new PersonalityProfile(
                    userId,
                    personalityType,
                    System.currentTimeMillis()
            );
            profile.setAverageDup(avgDup);
            profile.setCompletionRate(completionRate);
            profile.setTotalTasksCompleted(totalCompleted);
            profile.setLateSubmissions(lateSubmissions);
            personalityRepository.save(profile);

            var user = userRepository.findById(userId);
            if (user != null) {
                user.setPersonalityType(personalityType);
                userRepository.update(user);
            }
        }

        return DashboardResponse.builder()
                .averageDup(Math.round(avgDup * 100.0) / 100.0)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .personalityType(personalityType)
                .recommendation(recommendation)
                .totalTasksCompleted(totalCompleted)
                .lateSubmissions(lateSubmissions)
                .recentTasks(recentTasks)
                .build();
    }

    private double calculateAverageDUP(List<Task> tasks) {
        if (tasks.isEmpty()) return 0;
        return tasks.stream()
                .mapToDouble(Task::getDup)
                .average()
                .orElse(0);
    }

    private double calculateCompletionRate(List<Task> tasks) {
        if (tasks.isEmpty()) return 0;
        long completed = tasks.stream()
                .filter(t -> "completed".equals(t.getStatus()))
                .count();
        return (double) completed / tasks.size() * 100;
    }
}