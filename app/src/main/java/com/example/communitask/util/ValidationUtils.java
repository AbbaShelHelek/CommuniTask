package com.example.communitask.util;

import android.util.Patterns;

/**
 * Shared boolean validation helpers for user-entered data.
 */
public final class ValidationUtils {

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_TASK_TITLE_LENGTH = 3;

    private ValidationUtils() {
    }

    public static boolean isRequiredTextValid(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isEmailValid(String email) {
        return isRequiredTextValid(email) && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    public static boolean isPasswordValid(String password) {
        return isRequiredTextValid(password) && password.length() >= MIN_PASSWORD_LENGTH;
    }

    public static boolean isTaskTitleValid(String title) {
        return isRequiredTextValid(title) && title.trim().length() >= MIN_TASK_TITLE_LENGTH;
    }
}
