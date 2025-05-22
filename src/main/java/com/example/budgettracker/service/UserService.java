package com.example.budgettracker.service;

import com.example.budgettracker.dto.request.RegistrationRequest;
import com.example.budgettracker.exception.DuplicateResourceException;
import com.example.budgettracker.model.User;
import com.example.budgettracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(RegistrationRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username is already taken.");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email is already registered.");
        }

        Set<String> roles = new HashSet<>();
        roles.add("USER");

        User user = new User(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                roles
        );

        userRepository.save(user);
    }
}
