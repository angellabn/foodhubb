package com.foodhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Basic security configuration for FoodHub demo.
 * <p>
 * - Provides an in‑memory user (username: {@code user}, password: {@code password}).
 * - Enables form‑based login with Spring Security's default login page.
 * - Allows unauthenticated access to static resources, the health endpoint, and the currency conversion endpoint.
 * - Secures all other API routes, requiring authentication.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Permit static assets and health/convert endpoints without auth
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/static/**", "/api/health", "/api/convert/**").permitAll()
                .anyRequest().authenticated()
            )
            // Disable CSRF for simplicity (demo only)
            .csrf(csrf -> csrf.disable())
            // Enable form login with default page and HTTP Basic for API clients
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults())
            // Enable logout support
            .logout(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService users() {
        // For demo purposes we use a plain‑text password encoder. In production replace with a stronger encoder.
        return new InMemoryUserDetailsManager(
                User.withUsername("user")
                    .password("password")
                    .roles("USER")
                    .build()
        );
    }

    @Bean
    @SuppressWarnings("deprecation") // NoOpPasswordEncoder is deprecated but convenient for a demo.
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
