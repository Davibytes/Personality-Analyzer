package com.deadline.analyzer.service;

import com.deadline.analyzer.model.Task;
import com.deadline.analyzer.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalityAnalysisService {

    private final TaskService taskService;
    private final UserService userService;

    /**
     * Analyze user behavior and assign personality type
     */
    public PersonalityAnalysis analyzeUserPersonality(String userId) {
        List<Task> userTasks = taskService.getUserTasks(userId);

        if (userTasks == null || userTasks.isEmpty()) {
            return null;
        }

        PersonalityAnalysis analysis = new PersonalityAnalysis();

        // Calculate DUP Score (Deadline Utilization Percentage)
        double avgDup = calculateAverageDUP(userTasks);
        analysis.setAverageDup(avgDup);

        // Calculate completion rate
        int completionRate = calculateCompletionRate(userTasks);
        analysis.setCompletionRate(completionRate);

        // Calculate late submission percentage
        double latePercentage = calculateLatePercentage(userTasks);
        analysis.setLatePercentage(latePercentage);

        // Calculate on-time submissions
        long onTimeCount = userTasks.stream()
                .filter(t -> t.getIsCompleted() && !t.getIsLate())
                .count();
        analysis.setOnTimeSubmissions((int) onTimeCount);

        // Calculate late submissions
        long lateCount = userTasks.stream()
                .filter(t -> t.getIsCompleted() && t.getIsLate())
                .count();
        analysis.setLateSubmissions((int) lateCount);

        // Count total tasks
        analysis.setTotalTasks(userTasks.size());

        // Determine personality type
        String personalityType = determinePersonalityType(avgDup, completionRate, latePercentage, userTasks);
        analysis.setPersonalityType(personalityType);

        // Get recommendation based on personality
        String recommendation = getRecommendation(personalityType);
        analysis.setRecommendation(recommendation);

        return analysis;
    }

    /**
     * Calculate average DUP score across all completed tasks
     */
    private double calculateAverageDUP(List<Task> tasks) {
        List<Task> completedTasks = tasks.stream()
                .filter(t -> t.getIsCompleted() && t.getDupScore() != null)
                .toList();

        if (completedTasks.isEmpty()) {
            return 0.0;
        }

        return completedTasks.stream()
                .mapToDouble(Task::getDupScore)
                .average()
                .orElse(0.0);
    }

    /**
     * Calculate completion rate (percentage of completed tasks)
     */
    private int calculateCompletionRate(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return 0;
        }

        long completedCount = tasks.stream()
                .filter(Task::getIsCompleted)
                .count();

        return (int) ((completedCount * 100) / tasks.size());
    }

    /**
     * Calculate late submission percentage
     */
    private double calculateLatePercentage(List<Task> tasks) {
        List<Task> completedTasks = tasks.stream()
                .filter(Task::getIsCompleted)
                .toList();

        if (completedTasks.isEmpty()) {
            return 0.0;
        }

        long lateCount = completedTasks.stream()
                .filter(Task::getIsLate)
                .count();

        return (lateCount * 100.0) / completedTasks.size();
    }

    /**
     * Determine personality type based on analysis
     *
     * Rules:
     * 1. Last-Minute Sprinter: DUP >= 70% AND Completion Rate >= 80%
     * 2. Panic Starter: DUP >= 70% AND Completion Rate < 80%
     * 3. Perfectionist Delayer: DUP < 40% AND High number of edits
     * 4. Chronic Postponer: Late Percentage > 50%
     * 5. Steady Planner: Everything else (balanced)
     */
    private String determinePersonalityType(double avgDup, int completionRate,
                                            double latePercentage, List<Task> tasks) {

        // Rule 1: Last-Minute Sprinter
        if (avgDup >= 70 && completionRate >= 80) {
            return "Last-Minute Sprinter";
        }

        // Rule 2: Panic Starter
        if (avgDup >= 70 && completionRate < 80) {
            return "Panic Starter";
        }

        // Rule 3: Perfectionist Delayer (low DUP, meaning they start early)
        if (avgDup < 40 && completionRate >= 85) {
            return "Perfectionist Delayer";
        }

        // Rule 4: Chronic Postponer (more than 50% late submissions)
        if (latePercentage > 50) {
            return "Chronic Postponer";
        }

        // Rule 5: Steady Planner (default, balanced approach)
        return "Steady Planner";
    }

    /**
     * Get personalized recommendation based on personality type
     */
    private String getRecommendation(String personalityType) {
        return switch (personalityType) {
            case "Last-Minute Sprinter" ->
                    "You thrive under pressure and deliver quality work! " +
                            "However, avoid relying too much on pressure. Try starting one task earlier " +
                            "to improve your flexibility and reduce stress.";

            case "Panic Starter" ->
                    "You tend to wait too long before starting tasks, which affects your quality. " +
                            "Try using the '48-hour rule': Set an internal deadline 48 hours before the actual deadline. " +
                            "This gives you a safety buffer for unexpected issues.";

            case "Perfectionist Delayer" ->
                    "You start early and take your time, but sometimes get stuck perfecting. " +
                            "Try setting a 'good enough' threshold. Not everything needs to be perfect. " +
                            "Move on and submit on time rather than missing deadlines for perfectionism.";

            case "Chronic Postponer" ->
                    "You consistently struggle with meeting deadlines. " +
                            "Break tasks into smaller sub-tasks with their own deadlines. " +
                            "Also, try the 'accountability buddy' approach - tell someone about your deadline.";

            case "Steady Planner" ->
                    "Excellent! You have a balanced approach to deadline management. " +
                            "Keep doing what you're doing. Your consistent performance is a strength. " +
                            "Consider helping others develop better deadline habits.";

            default -> "Keep tracking your tasks to get better insights!";
        };
    }

    /**
     * Inner class to hold analysis results
     */
    public static class PersonalityAnalysis {
        private String personalityType;
        private Double averageDup;
        private Integer completionRate;
        private Double latePercentage;
        private Integer onTimeSubmissions;
        private Integer lateSubmissions;
        private Integer totalTasks;
        private String recommendation;

        // Getters and Setters
        public String getPersonalityType() {
            return personalityType;
        }

        public void setPersonalityType(String personalityType) {
            this.personalityType = personalityType;
        }

        public Double getAverageDup() {
            return averageDup;
        }

        public void setAverageDup(Double averageDup) {
            this.averageDup = averageDup;
        }

        public Integer getCompletionRate() {
            return completionRate;
        }

        public void setCompletionRate(Integer completionRate) {
            this.completionRate = completionRate;
        }

        public Double getLatePercentage() {
            return latePercentage;
        }

        public void setLatePercentage(Double latePercentage) {
            this.latePercentage = latePercentage;
        }

        public Integer getOnTimeSubmissions() {
            return onTimeSubmissions;
        }

        public void setOnTimeSubmissions(Integer onTimeSubmissions) {
            this.onTimeSubmissions = onTimeSubmissions;
        }

        public Integer getLateSubmissions() {
            return lateSubmissions;
        }

        public void setLateSubmissions(Integer lateSubmissions) {
            this.lateSubmissions = lateSubmissions;
        }

        public Integer getTotalTasks() {
            return totalTasks;
        }

        public void setTotalTasks(Integer totalTasks) {
            this.totalTasks = totalTasks;
        }

        public String getRecommendation() {
            return recommendation;
        }

        public void setRecommendation(String recommendation) {
            this.recommendation = recommendation;
        }
    }
}