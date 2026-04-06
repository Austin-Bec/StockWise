package com.stockwise.user;

import jakarta.persistence.*;

/**
 * Entity: User
 * Responsibility:
 * - Represents an authenticated application user.
 * - Stores username, hashed password, and role for authorization.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 30)
    private String role;

    protected User() {
        // JPA only
    }

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Stores a BCrypt-hashed password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    /**
     * Expected format: ROLE_USER or ROLE_ADMIN
     */
    public void setRole(String role) {
        this.role = role;
    }
}