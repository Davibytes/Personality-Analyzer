package com.deadline.analyzer.util;

import com.deadline.analyzer.model.Task;
import java.util.List;

public class PersonalityClassifier {

    public static String classifyPersonality(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return Constants.PersonalityTypes.STEADY_PLANNER;
        }

        double avgDup = calculateAverageDUP(tasks);
        int lateCount = countLateSubmissions(tasks);
        double latePercentage = (double) lateCount / tasks.size() * 100;
        int perfectionistCount = countPerfectionists(tasks);
        int successCount = countSuccessfulTasks(tasks);
        double successRate = (double) successCount / tasks.size() * 100;

        // Chronic Postponer
        if (latePercentage > 60 && avgDup > 60) {
            return Constants.PersonalityTypes.CHRONIC_POSTPONER;
        }

        // Panic Starter or Last-Minute Sprinter
        if (avgDup >= Constants.DUPThresholds.PANIC) {
            if (successRate >= 80) {
                return Constants.PersonalityTypes.LAST_MINUTE;
            }
            return Constants.PersonalityTypes.PANIC_STARTER;
        }

        // Perfectionist
        if (avgDup < 40 && perfectionistCount > tasks.size() / 2) {
            return Constants.PersonalityTypes.PERFECTIONIST;
        }

        // Steady Planner
        if (avgDup >= Constants.DUPThresholds.STEADY_MIN &&
                avgDup <= Constants.DUPThresholds.STEADY_MAX) {
            return Constants.PersonalityTypes.STEADY_PLANNER;
        }

        return Constants.PersonalityTypes.STEADY_PLANNER;
    }

    public static String getRecommendation(String personalityType) {
        switch (personalityType) {
            case Constants.PersonalityTypes.PANIC_STARTER:
                return "Break tasks into 3 sub-deadlines. Set early reminder notifications.";
            case Constants.PersonalityTypes.PERFECTIONIST:
                return "Set submission buffer time (24-48 hours). Focus on 'good enough'.";
            case Constants.PersonalityTypes.CHRONIC_POSTPONER:
                return "Schedule mandatory start reminder at 30% deadline. Use accountability partners.";
            case Constants.PersonalityTypes.LAST_MINUTE:
                return "You excel under pressure! Create buffer time for emergencies.";
            case Constants.PersonalityTypes.STEADY_PLANNER:
                return "Great job! Maintain your consistent approach. Help others adopt similar habits.";
            default:
                return "Track more tasks to get personalized recommendations.";
        }
    }

    private static double calculateAverageDUP(List<Task> tasks) {
        double totalDUP = tasks.stream()
                .mapToDouble(Task::getDup)
                .sum();
        return tasks.size() > 0 ? totalDUP / tasks.size() : 0;
    }

    private static int countLateSubmissions(List<Task> tasks) {
        return (int) tasks.stream()
                .filter(Task::isLateSubmission)
                .count();
    }

    private static int countPerfectionists(List<Task> tasks) {
        return (int) tasks.stream()
                .filter(task -> task.getDup() < 40 && task.getEditCount() >= 10)
                .count();
    }

    private static int countSuccessfulTasks(List<Task> tasks) {
        return (int) tasks.stream()
                .filter(task -> Constants.TaskStatus.COMPLETED.equals(task.getStatus())
                        && !task.isLateSubmission())
                .count();
    }
}