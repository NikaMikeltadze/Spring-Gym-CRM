package com.gym.crm.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.function.Function;

@Component
public class UsernamePasswordGenerator {

    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;
    private final SecureRandom random = new SecureRandom();

    /**
     * Generates a username from first and last name in format: FirstName.LastName
     * If username already exists, adds a serial number suffix (e.g., John.Smith1)
     *
     * @param firstName the first name
     * @param lastName the last name
     * @param existsChecker function to check if username already exists
     * @return generated unique username
     */
    public String generateUsername(String firstName, String lastName, Function<String, Boolean> existsChecker) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }

        String baseUsername = firstName.trim() + "." + lastName.trim();
        String username = baseUsername;
        int serialNumber = 1;

        while (existsChecker.apply(username)) {
            username = baseUsername + serialNumber;
            serialNumber++;
        }

        return username;
    }

    /**
     * Generates a random 10-character password
     *
     * @return random password
     */
    public String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(PASSWORD_CHARS.length());
            password.append(PASSWORD_CHARS.charAt(index));
        }
        return password.toString();
    }
}
