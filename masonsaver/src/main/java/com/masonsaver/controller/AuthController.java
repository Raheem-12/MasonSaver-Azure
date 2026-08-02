package com.masonsaver.controller;

import com.masonsaver.model.User;
import com.masonsaver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

/**
 * AuthController
 *
 * Handles all authentication related HTTP requests.
 * Exposes two endpoints:
 *   POST /api/auth/register — creates a new user account
 *   POST /api/auth/login    — verifies credentials and logs a user in
 *
 * @RestController tells Spring this class handles HTTP requests
 *                 and returns JSON responses automatically.
 * @RequestMapping sets the base URL path for all endpoints in this class.
 * @CrossOrigin allows requests from the HTML frontend running on a
 *              different port (e.g. Live Server on port 5500).
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    /**
     * UserRepository instance injected by Spring automatically.
     * Used to save new users and look up existing ones.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * BCryptPasswordEncoder
     *
     * Used to hash passwords before saving them to the database,
     * and to verify a submitted password against the stored hash
     * during login. Never stores or compares plain text passwords.
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /* -------------------------------------------------------------------------
     * POST /api/auth/register
     * -------------------------------------------------------------------------
     * Registers a new user account.
     *
     * Expects a JSON body with:
     *   {
     *     "fullName": "Jane Smith",
     *     "email":    "jane@example.com",
     *     "password": "mypassword123"
     *   }
     *
     * Returns:
     *   200 OK    — "User registered successfully"
     *   400 Bad Request — "Email already registered" if duplicate
     * ------------------------------------------------------------------------- */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> body) {

        String fullName = body.get("fullName");
        String email    = body.get("email");
        String password = body.get("password");

        /* Check if a user with this email already exists */
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        /* Hash the password before saving — never store plain text */
        String hashedPassword = passwordEncoder.encode(password);

        /* Create and save the new user to the database */
        User newUser = new User(fullName, email, hashedPassword);
        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }

    /* -------------------------------------------------------------------------
     * POST /api/auth/login
     * -------------------------------------------------------------------------
     * Verifies a user's credentials.
     *
     * Expects a JSON body with:
     *   {
     *     "email":    "jane@example.com",
     *     "password": "mypassword123"
     *   }
     *
     * Returns:
     *   200 OK          — "Login successful"
     *   400 Bad Request — "Invalid email or password" if credentials are wrong
     * ------------------------------------------------------------------------- */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> body) {

        String email    = body.get("email");
        String password = body.get("password");

        /* Look up the user by email */
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        User user = userOptional.get();

        /* Compare the submitted password against the stored hash */
        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
        if (!passwordMatches) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        return ResponseEntity.ok("Login successful");
    }
}