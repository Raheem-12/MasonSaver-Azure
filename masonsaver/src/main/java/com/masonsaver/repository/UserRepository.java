package com.masonsaver.repository;

import com.masonsaver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * UserRepository
 *
 * Handles all database operations for the User entity.
 * By extending JpaRepository, Spring automatically provides
 * standard operations like save(), findById(), findAll(),
 * and delete() with no extra code needed.
 *
 * The two type parameters are:
 *   User — the entity this repository manages
 *   Long — the type of the primary key (the id field in User.java)
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * findByEmail
     *
     * Looks up a user by their email address.
     * Spring automatically generates the SQL query from the method name:
     * SELECT * FROM users WHERE email = ?
     *
     * Returns an Optional so the caller can safely handle
     * the case where no user with that email exists.
     *
     * Used by AuthController to:
     *   1. Check if an email is already registered during signup
     *   2. Find the user record during login to verify the password
     *
     * @param email the email address to search for
     * @return an Optional containing the User if found, or empty if not
     */
    Optional<User> findByEmail(String email);
}