package com.example.phase2.application.validation;

import org.springframework.stereotype.Component;

@Component
public class AdminPasswordPolicyValidator {

    private static final int MIN_PASSWORD_LENGTH = 8;

    public void validateNewPassword(String newPassword) {
        String requiredPassword = requireNonBlank(newPassword, "newPassword");

        if (requiredPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "newPassword must be at least " + MIN_PASSWORD_LENGTH + " characters long"
            );
        }
        if (!containsUppercase(requiredPassword)) {
            throw new IllegalArgumentException("newPassword must contain at least one uppercase letter");
        }
        if (!containsLowercase(requiredPassword)) {
            throw new IllegalArgumentException("newPassword must contain at least one lowercase letter");
        }
        if (!containsDigit(requiredPassword)) {
            throw new IllegalArgumentException("newPassword must contain at least one digit");
        }
    }

    public void validatePasswordConfirmation(String newPassword, String confirmPassword) {
        String requiredNewPassword = requireNonBlank(newPassword, "newPassword");
        String requiredConfirmation = requireNonBlank(confirmPassword, "confirmPassword");

        if (!requiredNewPassword.equals(requiredConfirmation)) {
            throw new IllegalArgumentException("Password confirmation does not match newPassword");
        }
    }

    public void validatePasswordChange(String newPassword, String confirmPassword) {
        validateNewPassword(newPassword);
        validatePasswordConfirmation(newPassword, confirmPassword);
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    private static boolean containsUppercase(String value) {
        return value.chars().anyMatch(Character::isUpperCase);
    }

    private static boolean containsLowercase(String value) {
        return value.chars().anyMatch(Character::isLowerCase);
    }

    private static boolean containsDigit(String value) {
        return value.chars().anyMatch(Character::isDigit);
    }
}
