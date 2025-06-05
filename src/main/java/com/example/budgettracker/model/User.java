package com.example.budgettracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a registered user of the Budget Tracker application.
 * Each user has a unique username and email, a password, and one or more roles.
 */
@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_email", columnList = "email")
    }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username for login and display purposes.
     */
    @NotBlank(message = "Username is required")
    @Column(unique = true)
    private String username;

    /**
     * Unique email used for account identification and communication.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    @Column(unique = true)
    private String email;

    /**
     * Encrypted password stored securely (bcrypt).
     */
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Roles assigned to the user. For example: USER, ADMIN.
     * Stored as a simple set of strings.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = new HashSet<>();

    // Constructors
    public User() {}

    public User(String username, String email, String password, Set<String> roles) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = (roles != null) ? roles : new HashSet<>();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
