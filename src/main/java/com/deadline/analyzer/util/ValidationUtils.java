package com.deadline.analyzer.util;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        return hasUppercase && hasDigit;
    }

    public static boolean isNotEmpty(String field) {
        return field != null && !field.trim().isEmpty();
    }

    public static String getPasswordErrorMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one number";
        }
        return null;
    }

    public static boolean isValidTaskTitle(String title) {
        return title != null && title.trim().length() >= 3 && title.trim().length() <= 200;
    }

    public static boolean isValidDeadline(long deadline) {
        return deadline > System.currentTimeMillis();
    }
}