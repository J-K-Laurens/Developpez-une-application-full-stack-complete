package com.openclassrooms.mddapi.validation;

public class PasswordValidator {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";

    /**
     * Valide le mot de passe selon les critères :
     * - Au moins 8 caractères
     * - Au moins un chiffre
     * - Au moins une lettre minuscule
     * - Au moins une lettre majuscule
     * - Au moins un caractère spécial
     *
     * @param password le mot de passe à valider
     * @return true si le mot de passe est valide, false sinon
     */
    public static boolean isValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return password.matches(PASSWORD_PATTERN);
    }

    /**
     * Retourne un message d'erreur détaillé si le mot de passe ne respecte pas les critères
     *
     * @param password le mot de passe à valider
     * @return un message d'erreur ou null si le mot de passe est valide
     */
    public static String getErrorMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "Le mot de passe est obligatoire.";
        }

        StringBuilder errors = new StringBuilder();

        if (password.length() < 8) {
            errors.append("- Au moins 8 caractères\n");
        }

        if (!password.matches(".*[0-9].*")) {
            errors.append("- Au moins un chiffre\n");
        }

        if (!password.matches(".*[a-z].*")) {
            errors.append("- Au moins une lettre minuscule\n");
        }

        if (!password.matches(".*[A-Z].*")) {
            errors.append("- Au moins une lettre majuscule\n");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            errors.append("- Au moins un caractère spécial (!@#$%^&*...)\n");
        }

        if (errors.length() == 0) {
            return null; // Valide
        }

        return "Le mot de passe doit respecter les critères suivants :\n" + errors.toString().trim();
    }
}
