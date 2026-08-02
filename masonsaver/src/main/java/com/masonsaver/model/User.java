package com.masonsaver.model;

import jakarta.persistence.*;

/**
 * User
 *
 * Represents a registered MasonSaver user.
 * This class is a JPA Entity, meaning Spring will automatically
 * create a corresponding "users" table in PostgreSQL based on
 * the fields defined here.
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Primary key for the users table.
     * IDENTITY strategy means PostgreSQL auto-increments this value
     * every time a new user is inserted.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user's full name.
     * Cannot be null in the database.
     */
    @Column(nullable = false)
    private String fullName;

    /**
     * The user's email address.
     * Must be unique across all users (no duplicate accounts).
     * Cannot be null in the database.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * The user's hashed password.
     * NEVER store plain text passwords.
     * This field stores the BCrypt hash produced by AuthService.
     * Cannot be null in the database.
     */
    @Column(nullable = false)
    private String password;

    /* -------------------------
     * Constructors
     * ------------------------- */

    /** Default no-argument constructor required by JPA */
    public User() {}

    /**
     * Constructor for creating a new user.
     *
     * @param fullName The user's full name
     * @param email    The user's email address
     * @param password The user's hashed password (not plain text)
     */
    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email    = email;
        this.password = password;
    }

    /* -------------------------
     * Getters and Setters
     * ------------------------- */

    /** @return the user's database ID */
    public Long getId() { return id; }

    /** @return the user's full name */
    public String getFullName() { return fullName; }

    /** @param fullName the full name to set */
    public void setFullName(String fullName) { this.fullName = fullName; }

    /** @return the user's email address */
    public String getEmail() { return email; }

    /** @param email the email address to set */
    public void setEmail(String email) { this.email = email; }

    /** @return the user's hashed password */
    public String getPassword() { return password; }

    /** @param password the hashed password to set */
    public void setPassword(String password) { this.password = password; }
}