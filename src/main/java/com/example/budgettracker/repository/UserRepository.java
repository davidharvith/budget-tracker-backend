package com.example.budgettracker.repository;

import com.example.budgettracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link User} entities.
 * Provides standard CRUD operations via {@link JpaRepository},
 * along with custom methods for user authentication and registration validation.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     * Used primarily for login and token-based authentication.
     *
     * @param username the unique username
     * @return an {@link Optional} containing the user if found, or empty if not
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks whether a username already exists.
     * Useful for validation during registration.
     *
     * @param username the username to check
     * @return true if a user with that username exists
     */
    Boolean existsByUsername(String username);

    /**
     * Checks whether an email is already associated with a user.
     * Helps prevent duplicate email registrations.
     *
     * @param email the email to check
     * @return true if the email is already in use
     */
    Boolean existsByEmail(String email);
}
