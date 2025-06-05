package com.example.budgettracker.service;

import com.example.budgettracker.dto.request.RegistrationRequest;
import com.example.budgettracker.exception.DuplicateResourceException;
import com.example.budgettracker.model.User;
import com.example.budgettracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link UserService}.
 *
 * These tests verify registration behavior such as:
 * - Successful registration
 * - Duplicate username/email handling
 * - Password encoding
 */
class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    /**
     * Set up mocks and service before each test.
     */
    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    /**
     * Test: Successful registration.
     *
     * Verifies that a user is saved correctly with encoded password
     * when no duplicate username/email exists.
     */
    @Test
    void registerUser_success() {
        RegistrationRequest request = new RegistrationRequest("user1", "user1@email.com", "password123");

        // Mock repository responses
        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

        // Call the method under test
        userService.registerUser(request);

        // Capture the user saved to the repository
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        // Validate saved fields
        assertEquals("user1", savedUser.getUsername());
        assertEquals("user1@email.com", savedUser.getEmail());
        assertEquals("hashedPassword", savedUser.getPassword());
    }

    /**
     * Test: Registration fails due to duplicate username.
     *
     * Verifies that an exception is thrown and no user is saved.
     */
    @Test
    void registerUser_duplicateUsername_throwsException() {
        RegistrationRequest request = new RegistrationRequest("user1", "user1@email.com", "password123");

        when(userRepository.existsByUsername("user1")).thenReturn(true);

        DuplicateResourceException ex = assertThrows(
                DuplicateResourceException.class,
                () -> userService.registerUser(request)
        );

        assertEquals("Username is already taken.", ex.getMessage());

        // Ensure save is never called
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test: Registration fails due to duplicate email.
     *
     * Verifies that an exception is thrown and no user is saved.
     */
    @Test
    void registerUser_duplicateEmail_throwsException() {
        RegistrationRequest request = new RegistrationRequest("user1", "user1@email.com", "password123");

        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@email.com")).thenReturn(true);

        DuplicateResourceException ex = assertThrows(
                DuplicateResourceException.class,
                () -> userService.registerUser(request)
        );

        assertEquals("Email is already registered.", ex.getMessage());

        // Ensure save is never called
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test: Password is encoded before being saved.
     *
     * Verifies that the plain-text password is passed through
     * the password encoder before persisting.
     */
    @Test
    void registerUser_passwordIsEncoded() {
        RegistrationRequest request = new RegistrationRequest("user2", "user2@email.com", "mypassword");

        when(userRepository.existsByUsername("user2")).thenReturn(false);
        when(userRepository.existsByEmail("user2@email.com")).thenReturn(false);
        when(passwordEncoder.encode("mypassword")).thenReturn("encodedPassword");

        userService.registerUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("encodedPassword", savedUser.getPassword());
    }
}
