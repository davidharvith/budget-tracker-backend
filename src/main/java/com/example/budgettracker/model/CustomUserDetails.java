package com.example.budgettracker.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * 
 * This class adapts the application's User entity to the contract expected
 * by Spring Security for authentication and authorization.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    /**
     * Constructor to initialize the wrapper with the application-specific User.
     *
     * @param user the domain user entity
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Returns the authorities granted to the user. In this case, each role
     * is prefixed with "ROLE_" to match Spring Security conventions.
     *
     * @return a collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }

    /**
     * Returns the hashed password used to authenticate the user.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username used to authenticate the user.
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Indicates whether the user's account has expired.
     * Always returns true for simplicity (custom logic could be added here).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * Always returns true for simplicity.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     * Always returns true for simplicity.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     * Always returns true for simplicity.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Returns the underlying User entity, useful for accessing app-specific fields.
     */
    public User getUser() {
        return user;
    }
}
