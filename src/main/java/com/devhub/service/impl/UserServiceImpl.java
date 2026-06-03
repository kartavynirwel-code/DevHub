package com.devhub.service.impl;

import com.devhub.dto.UserRegistrationDto;
import com.devhub.entity.User;
import com.devhub.exception.ResourceNotFoundException;
import com.devhub.repository.UserRepository;
import com.devhub.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Slf4j
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor Injection (SOLID Guidelines & Best Practices)
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Naye user ki details validated DTO se lekar database me save karta hai.
     * Password encryption ke liye BCrypt internally call hota hai.
     */
    @Override
    @Transactional
    public void registerUser(UserRegistrationDto dto) {
        log.info("Attempting to register user with username: {}", dto.getUsername());

        // Business Rule validation checks
        if (userRepository.existsByUsername(dto.getUsername())) {
            log.warn("Registration failed: Username '{}' is already taken", dto.getUsername());
            throw new IllegalArgumentException("Username is already taken.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Registration failed: Email '{}' is already registered", dto.getEmail());
            throw new IllegalArgumentException("Email is already registered.");
        }

        // Entity Mapping & Object Building (Lombok Builder pattern)
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword())) // Password Encription
                .karmaPoints(0) // Explicit Initial Default
                .build();

        userRepository.save(user);
        log.info("User '{}' registered successfully into DevHub ecosystem.", user.getUsername());
    }

    /**
     * Spring Security internally is method ko call karta hai authentication flow check karne ke liye.
     * Database se database user match karke token build-up execute karta hai.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Spring Security fetching core credentials for user parsing: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Authentication check failed: User '{}' not found", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        // Spring Security Framework standard fully operational user entity pass map ho rahi hai
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>() // Habhi ke liye empty list rules (No Roles/Authorities assigned explicitly)
        );
    }
}