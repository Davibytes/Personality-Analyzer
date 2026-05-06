package com.deadline.analyzer.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    public List<String> getRecommendations(String personalityType) {
        return switch (personalityType) {
            case "Last-Minute Sprinter" -> getLastMinuteSprintRecommendations();
            case "Panic Starter" -> getPanicStarterRecommendations();
            case "Perfectionist Delayer" -> getPerfectionistDelayerRecommendations();
            case "Chronic Postponer" -> getChronicPostponerRecommendations();
            case "Steady Planner" -> getSteadyPlannerRecommendations();
            default -> getGeneralRecommendations();
        };
    }

    private List<String> getLastMinuteSprintRecommendations() {
        return List.of(
                "You thrive under pressure and deliver quality work consistently.",
                "Try starting one task 48 hours earlier to test a new approach.",
                "Your strength: you work well when there's urgency and deliver quality.",
                "Your risk: one emergency could derail everything. Build buffer time.",
                "Set an internal deadline 24-48 hours earlier than the actual deadline.",
                "You could be even more productive with a small safety margin."
        );
    }

    private List<String> getPanicStarterRecommendations() {
        return List.of(
                "You wait too long before starting, which affects your quality.",
                "Use the '48-hour rule': Set an internal deadline 48 hours before the real one.",
                "Your issue: waiting causes stress and reduces work quality.",
                "Break large tasks into 3-4 smaller milestones with intermediate deadlines.",
                "Start your next task immediately after understanding the requirements.",
                "Track your 'start time' - try to start at least 1 week before deadline."
        );
    }

    private List<String> getPerfectionistDelayerRecommendations() {
        return List.of(
                "You start early and care about quality, which is great!",
                "However, perfectionism can cause you to miss deadlines.",
                "Set a 'good enough' threshold - not everything needs to be perfect.",
                "Use an internal deadline that's 3-5 days before the real deadline.",
                "When you hit your internal deadline, submit even if it's not 100% perfect.",
                "Remember: a submitted imperfect task beats a perfect missed deadline."
        );
    }

    private List<String> getChronicPostponerRecommendations() {
        return List.of(
                "You consistently struggle with meeting deadlines - this can change!",
                "Break tasks into smaller sub-tasks with their own intermediate deadlines.",
                "Use the 'accountability buddy' approach - tell someone about your deadline.",
                "Set calendar reminders at 50%, 75%, and 90% of time before deadline.",
                "Start today, not tomorrow - momentum is your friend.",
                "Track each completed task to build confidence and break the postponement cycle."
        );
    }

    private List<String> getSteadyPlannerRecommendations() {
        return List.of(
                "Excellent! You have a balanced and consistent approach to deadlines.",
                "Your consistent performance is your greatest strength.",
                "Keep maintaining your current schedule and habits - they work!",
                "Consider helping others develop better deadline management skills.",
                "Try taking on slightly more complex tasks to challenge yourself.",
                "Your approach could become a model for other students to follow."
        );
    }

    private List<String> getGeneralRecommendations() {
        return List.of(
                "Track more tasks to get better insights about your patterns.",
                "Create diverse types of tasks to see different deadline scenarios.",
                "Review your completed tasks weekly to identify patterns.",
                "Set realistic deadlines based on the task complexity.",
                "Celebrate your completed tasks - momentum builds success!",
                "Keep improving - every task completion makes you better!"
        );
    }
}