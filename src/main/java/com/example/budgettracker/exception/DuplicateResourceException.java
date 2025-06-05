package com.example.budgettracker.exception;

/**
 * Exception thrown when attempting to create a resource that already exists.
 * 
 * Example use cases:
 * - Registering a user with a username that already exists
 * - Creating a budget with a duplicate name (if enforced)
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Constructs a new DuplicateResourceException with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}
