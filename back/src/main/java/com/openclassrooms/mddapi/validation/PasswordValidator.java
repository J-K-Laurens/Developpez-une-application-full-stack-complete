package com.openclassrooms.mddapi.validation;

/**
 * Utility class for password validation.
 * Enforces password strength requirements.
 */
public class PasswordValidator {
    private static final String PASSWORD_PATTERN =

"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";

    /**
     * Validates the password according to the following criteria:
     * - At least 8 characters
     * - At least one digit
     * - At least one lowercase letter
     * - At least one uppercase letter
     * - At least one special character
     *
     * @param password the password to validate
     * @return true if password is valid, false otherwise
     */
    public static boolean isValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return password.matches(PASSWORD_PATTERN);
    }

    /**
     * Returns a detailed error message if the password does not meet the criteria.
     *
     * @param password the password to validate
     * @return an error message or null if password is valid
     */
    public static String getErrorMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is required.";
        }

        StringBuilder errors = new StringBuilder();

        if (password.length() < 8) {
            errors.append("- At least 8 characters\n");
        }

        if (!password.matches(".*[0-9].*")) {
            errors.append("- At least one digit\n");
        }

        if (!password.matches(".*[a-z].*")) {
            errors.append("- At least one lowercase letter\n");
        }

        if (!password.matches(".*[A-Z].*")) {
            errors.append("- At least one uppercase letter\n");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            errors.append("- At least one special character (!@#$%^&*...)\n");
        }

        if (errors.length() == 0) {
            return null; // Valid
        }

        return "The password must meet the following criteria:\n" + errors.toString().trim();
    }
}
