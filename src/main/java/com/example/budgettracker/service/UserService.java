package com.example.budgettracker.service;

import com.example.budgettracker.dto.request.RegistrationRequest;
import com.example.budgettracker.exception.DuplicateResourceException;
import com.example.budgettracker.model.User;
import com.example.budgettracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Service class for managing user-related operations such as registration.

 * Handles validation, role assignment, and secure password encoding.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user.
     * Validates username and email uniqueness, hashes the password,
     * and saves the new user with a default "USER" role.
     *
     * @param request the user registration request data
     * @throws DuplicateResourceException if the username or email is already in use
     */
    public void registerUser(RegistrationRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username is already taken.");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email is already registered.");
        }

        Set<String> roles = new HashSet<>();
        roles.add("USER"); // default role

        User user = new User(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                roles
        );

        userRepository.save(user);
    }
}
