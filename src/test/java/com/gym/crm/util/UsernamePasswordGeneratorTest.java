package com.gym.crm.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class UsernamePasswordGeneratorTest {

    private UsernamePasswordGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new UsernamePasswordGenerator();
    }

    @Test
    void generateUsername_Successful_NoSerial() {
        // Given
        String firstName = "John";
        String lastName = "Smith";
        Function<String, Boolean> existsChecker = s -> false;

        // When
        String result = generator.generateUsername(firstName, lastName, existsChecker);

        // Then
        assertEquals("John.Smith", result);
    }

    @Test
    void generateUsername_Successful_WithSerial() {
        // Given
        String firstName = "John";
        String lastName = "Smith";
        // Simulate John.Smith already exists
        Function<String, Boolean> existsChecker = s -> s.equals("John.Smith");

        // When
        String result = generator.generateUsername(firstName, lastName, existsChecker);

        // Then
        assertEquals("John.Smith1", result);
    }

    @Test
    void generateUsername_Successful_WithMultipleSerials() {
        // Given
        String firstName = "John";
        String lastName = "Smith";
        Set<String> existingUsernames = new HashSet<>();
        existingUsernames.add("John.Smith");
        existingUsernames.add("John.Smith1");
        existingUsernames.add("John.Smith2");

        Function<String, Boolean> existsChecker = existingUsernames::contains;

        // When
        String result = generator.generateUsername(firstName, lastName, existsChecker);

        // Then
        assertEquals("John.Smith3", result);
    }

    @Test
    void generateUsername_TrimsNames() {
        // Given
        String firstName = "  John  ";
        String lastName = "  Smith  ";
        Function<String, Boolean> existsChecker = s -> false;

        // When
        String result = generator.generateUsername(firstName, lastName, existsChecker);

        // Then
        assertEquals("John.Smith", result);
    }

    @Test
    void generateUsername_NullFirstName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            generator.generateUsername(null, "Smith", s -> false)
        );
    }

    @Test
    void generateUsername_EmptyFirstName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            generator.generateUsername("", "Smith", s -> false)
        );
    }

    @Test
    void generateUsername_BlankFirstName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            generator.generateUsername("   ", "Smith", s -> false)
        );
    }

    @Test
    void generateUsername_NullLastName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            generator.generateUsername("John", null, s -> false)
        );
    }

    @Test
    void generateUsername_EmptyLastName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            generator.generateUsername("John", "", s -> false)
        );
    }

    @Test
    void generateUsername_BlankLastName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            generator.generateUsername("John", "   ", s -> false)
        );
    }

    @Test
    void generatePassword_ReturnsCorrectLength() {
        String password = generator.generatePassword();
        assertEquals(10, password.length());
    }

    @Test
    void generatePassword_ReturnsRandomPasswords() {
        String password1 = generator.generatePassword();
        String password2 = generator.generatePassword();
        assertNotEquals(password1, password2);
    }

    @Test
    void generatePassword_ContainsAllowedCharactersOnly() {
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String password = generator.generatePassword();
        for (char c : password.toCharArray()) {
            assertTrue(allowedChars.indexOf(c) >= 0, "Password contains invalid character: " + c);
        }
    }
}

