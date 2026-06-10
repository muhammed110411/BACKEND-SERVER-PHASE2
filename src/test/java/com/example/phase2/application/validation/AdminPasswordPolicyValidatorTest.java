package com.example.phase2.application.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminPasswordPolicyValidatorTest {

    private AdminPasswordPolicyValidator adminPasswordPolicyValidator;

    @BeforeEach
    void setUp() {
        adminPasswordPolicyValidator = new AdminPasswordPolicyValidator();
    }

    @Test
    void validateNewPasswordRejectsBlankPassword() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> adminPasswordPolicyValidator.validateNewPassword(" ")
        );

        assertEquals("newPassword must not be blank", exception.getMessage());
    }

    @Test
    void validateNewPasswordRejectsPasswordShorterThanMinimumLength() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> adminPasswordPolicyValidator.validateNewPassword("Abc123")
        );

        assertEquals("newPassword must be at least 8 characters long", exception.getMessage());
    }

    @Test
    void validateNewPasswordRejectsPasswordWithoutUppercaseLetter() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> adminPasswordPolicyValidator.validateNewPassword("password1")
        );

        assertEquals("newPassword must contain at least one uppercase letter", exception.getMessage());
    }

    @Test
    void validateNewPasswordRejectsPasswordWithoutLowercaseLetter() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> adminPasswordPolicyValidator.validateNewPassword("PASSWORD1")
        );

        assertEquals("newPassword must contain at least one lowercase letter", exception.getMessage());
    }

    @Test
    void validateNewPasswordRejectsPasswordWithoutDigit() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> adminPasswordPolicyValidator.validateNewPassword("Password")
        );

        assertEquals("newPassword must contain at least one digit", exception.getMessage());
    }

    @Test
    void validatePasswordConfirmationRejectsMismatchedConfirmation() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> adminPasswordPolicyValidator.validatePasswordConfirmation("Password1", "Password2")
        );

        assertEquals("Password confirmation does not match newPassword", exception.getMessage());
    }

    @Test
    void validatePasswordChangeAllowsPolicyCompliantPassword() {
        assertDoesNotThrow(() -> adminPasswordPolicyValidator.validatePasswordChange("Password1", "Password1"));
    }
}
