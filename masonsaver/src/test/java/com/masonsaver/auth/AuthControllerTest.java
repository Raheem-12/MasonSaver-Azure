package com.masonsaver.auth;

import com.masonsaver.controller.AuthController;
import com.masonsaver.model.User;
import com.masonsaver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthControllerTest
 *
 * Unit tests for the register() and login() methods in AuthController.
 * Uses Mockito to mock UserRepository so no real database connection is needed.
 * Run with: mvn test
 */
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    /* Mocked dependency: replaces the real database with a fake */
    @Mock
    private UserRepository userRepository;

    /* The class under test with mocked dependencies injected automatically */
    @InjectMocks
    private AuthController authController;

    private Map<String, String> validRegisterBody;
    private Map<String, String> validLoginBody;
    private User existingUser;

    /**
     * setUp()
     * Runs before each test to initialize reusable test data.
     */
    @BeforeEach
    void setUp() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        validRegisterBody = new HashMap<>();
        validRegisterBody.put("fullName", "Jane Smith");
        validRegisterBody.put("email", "jane@example.com");
        validRegisterBody.put("password", "password123");

        validLoginBody = new HashMap<>();
        validLoginBody.put("email", "jane@example.com");
        validLoginBody.put("password", "password123");

        existingUser = new User("Jane Smith", "jane@example.com", encoder.encode("password123"));
    }

    /* ── register() tests ── */

    /**
     * A new email not in the database should register successfully with HTTP 200.
     */
    @Test
    void registerWithNewEmail_returns200AndSuccessMessage() {
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        ResponseEntity<String> response = authController.register(validRegisterBody);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("User registered successfully", response.getBody());
    }

    /**
     * A duplicate email should be rejected with HTTP 400.
     */
    @Test
    void registerWithDuplicateEmail_returns400AndErrorMessage() {
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(existingUser));

        ResponseEntity<String> response = authController.register(validRegisterBody);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Email already registered", response.getBody());
    }

    /**
     * The saved password must be a BCrypt hash, never plain text.
     */
    @Test
    void registerHashesPasswordBeforeSaving() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertNotEquals("password123", savedUser.getPassword());
            assertTrue(savedUser.getPassword().startsWith("$2a$"));
            return savedUser;
        });

        authController.register(validRegisterBody);
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * On successful registration the user must be saved to the repository exactly once.
     */
    @Test
    void registerSavesUserToRepository() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        authController.register(validRegisterBody);

        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * A duplicate email should never trigger a save to the repository.
     */
    @Test
    void registerWithMissingFields_returns400() {
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(existingUser));

        authController.register(validRegisterBody);

        verify(userRepository, never()).save(any(User.class));
    }

    /* ── login() tests ── */

    /**
     * Valid email and matching password should return HTTP 200.
     */
    @Test
    void loginWithValidCredentials_returns200() {
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(existingUser));

        ResponseEntity<String> response = authController.login(validLoginBody);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Login successful", response.getBody());
    }

    /**
     * An unregistered email should return HTTP 400.
     */
    @Test
    void loginWithUnregisteredEmail_returns400() {
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());

        ResponseEntity<String> response = authController.login(validLoginBody);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid email or password", response.getBody());
    }

    /**
     * A valid email with the wrong password should return HTTP 400.
     */
    @Test
    void loginWithWrongPassword_returns400() {
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(existingUser));

        Map<String, String> wrongPasswordBody = new HashMap<>();
        wrongPasswordBody.put("email", "jane@example.com");
        wrongPasswordBody.put("password", "wrongpassword");

        ResponseEntity<String> response = authController.login(wrongPasswordBody);

        assertEquals(400, response.getStatusCode().value());
    }

    /**
     * The error message for wrong password and unknown email must be identical
     * to avoid revealing which field was incorrect.
     */
    @Test
    void loginDoesNotRevealWhichFieldWasWrong() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(existingUser));

        Map<String, String> unknownEmailBody = new HashMap<>();
        unknownEmailBody.put("email", "unknown@example.com");
        unknownEmailBody.put("password", "password123");

        Map<String, String> wrongPasswordBody = new HashMap<>();
        wrongPasswordBody.put("email", "jane@example.com");
        wrongPasswordBody.put("password", "wrongpassword");

        ResponseEntity<String> emailResponse    = authController.login(unknownEmailBody);
        ResponseEntity<String> passwordResponse = authController.login(wrongPasswordBody);

        assertEquals(emailResponse.getBody(), passwordResponse.getBody());
    }

    /**
     * An empty password string should return HTTP 400.
     */
    @Test
    void loginWithEmptyPassword_returns400() {
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(existingUser));

        Map<String, String> emptyPasswordBody = new HashMap<>();
        emptyPasswordBody.put("email", "jane@example.com");
        emptyPasswordBody.put("password", "");

        ResponseEntity<String> response = authController.login(emptyPasswordBody);

        assertEquals(400, response.getStatusCode().value());
    }
}