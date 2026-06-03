package com.oneclick.repair.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Password Encoder: This automatically scrambles passwords using BCrypt hashing
    // string so that even if someone steals the database, they cannot read plain text passwords.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. The Security Gatekeeper rules
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF since we are building a stateless REST API using JWT tokens
            .csrf(csrf -> csrf.disable())
            
            // Define accessibility rules for endpoints
            .authorizeHttpRequests(auth -> auth
                // Allow anyone to access the login URL so they can get their token
                .requestMatchers("/api/auth/login").permitAll()
                
                // Purely public consumer zone routes (like browsing the catalog)
                .requestMatchers("/api/public/**").permitAll()
                
                // Lock down management paths strictly to ADMIN (Shop Owner)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/ledger/**").hasRole("ADMIN")
                .requestMatchers("/api/inventory/manage/**").hasRole("ADMIN")
                
                // Allow both ADMIN and TECHNICIAN to view or update field/home visit jobs
                .requestMatchers("/api/visits/**").hasAnyRole("ADMIN", "TECHNICIAN")
                
                // Any other request not explicitly mentioned requires a valid login
                .anyRequest().authenticated()
            );

        return http.build();
    }
}