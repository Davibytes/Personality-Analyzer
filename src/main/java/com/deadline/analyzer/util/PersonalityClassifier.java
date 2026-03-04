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

        return switch (tasks.size()) {
            case 0 -> Constants.PersonalityTypes.STEADY_PLANNER;
            default -> {
                // Chronic Postponer
                if (latePercentage > 60 && avgDup > 60) {
                    yield Constants.PersonalityTypes.CHRONIC_POSTPONER;
                }

                // Panic Starter or Last-Minute Sprinter
                if (avgDup >= Constants.DUPThresholds.PANIC) {
                    yield successRate >= 80
                            ? Constants.PersonalityTypes.LAST_MINUTE
                            : Constants.PersonalityTypes.PANIC_STARTER;
                }

                // Perfectionist
                if (avgDup < 40 && perfectionistCount > tasks.size() / 2) {
                    yield Constants.PersonalityTypes.PERFECTIONIST;
                }

                // Steady Planner
                if (avgDup >= Constants.DUPThresholds.STEADY_MIN &&
                        avgDup <= Constants.DUPThresholds.STEADY_MAX) {
                    yield Constants.PersonalityTypes.STEADY_PLANNER;
                }

                yield Constants.PersonalityTypes.STEADY_PLANNER;
            }
        };
    }

    public static String getRecommendation(String personalityType) {
        return switch (personalityType) {
            case Constants.PersonalityTypes.PANIC_STARTER ->
                    "Break tasks into 3 sub-deadlines. Set early reminder notifications.";
            case Constants.PersonalityTypes.PERFECTIONIST ->
                    "Set submission buffer time (24-48 hours). Focus on 'good enough'.";
            case Constants.PersonalityTypes.CHRONIC_POSTPONER ->
                    "Schedule mandatory start reminder at 30% deadline. Use accountability partners.";
            case Constants.PersonalityTypes.LAST_MINUTE ->
                    "You excel under pressure! Create buffer time for emergencies.";
            case Constants.PersonalityTypes.STEADY_PLANNER ->
                    "Great job! Maintain your consistent approach. Help others adopt similar habits.";
            default -> "Track more tasks to get personalized recommendations.";
        };
    }

    private static double calculateAverageDUP(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return 0;
        }
        double totalDUP = tasks.stream()
                .mapToDouble(Task::getDup)
                .sum();
        return totalDUP / tasks.size();
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