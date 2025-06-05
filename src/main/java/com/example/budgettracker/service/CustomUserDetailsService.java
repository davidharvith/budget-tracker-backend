package com.example.budgettracker.service;

import com.example.budgettracker.model.CustomUserDetails;
import com.example.budgettracker.model.User;
import com.example.budgettracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * Responsible for loading user-specific data for authentication.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads the user details from the database by username.
     * Called automatically by Spring Security during authentication.
     *
     * @param username the username used for login
     * @return UserDetails object containing user credentials and roles
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Wrap the user in a UserDetails implementation (for Spring Security)
        return new CustomUserDetails(user);
    }
}
