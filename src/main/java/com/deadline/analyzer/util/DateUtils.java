package com.deadline.analyzer.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public static long getCurrentTimestampUTC() {
        return System.currentTimeMillis();
    }

    public static String formatDate(long timestamp) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("UTC"))
                .format(FORMATTER);
    }

    public static String formatDateTime(long timestamp) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("UTC"))
                .format(DATETIME_FORMATTER);
    }

    public static double calculateDUP(long creationTime, long startTime, long deadlineTime) {
        if (startTime < creationTime || deadlineTime <= creationTime) {
            return 0;
        }
        long totalTime = deadlineTime - creationTime;
        long elapsedTime = startTime - creationTime;
        return ((double) elapsedTime / totalTime) * 100;
    }

    public static long getDaysRemaining(long deadlineTime) {
        long currentTime = getCurrentTimestampUTC();
        return (deadlineTime - currentTime) / (1000 * 60 * 60 * 24);
    }

    public static boolean isDeadlinePassed(long deadlineTime) {
        return getCurrentTimestampUTC() > deadlineTime;
    }

    public static String formatTimeRemaining(long deadlineTime) {
        long daysRemaining = getDaysRemaining(deadlineTime);
        if (daysRemaining < 0) return "Overdue";
        if (daysRemaining == 0) return "Due today";
        if (daysRemaining == 1) return "Due tomorrow";
        return daysRemaining + " days left";
    }
}