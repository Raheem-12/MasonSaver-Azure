package com.masonsaver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig
 *
 * Configures Spring Security for the MasonSaver application.
 * By default Spring Security blocks ALL endpoints and requires
 * a login. This config overrides that behavior to allow our
 * register and login endpoints to be publicly accessible,
 * while still protecting other routes in the future.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * securityFilterChain
     *
     * Defines which endpoints are public and which require authentication.
     *
     * Current rules:
     *   /api/auth/register — public, anyone can call this to create an account
     *   /api/auth/login    — public, anyone can call this to sign in
     *   everything else    — requires authentication (for future protected routes)
     *
     * Also disables CSRF protection for now since we are calling the API
     * from plain HTML fetch() requests which do not include CSRF tokens.
     * This is acceptable for development but should be revisited before
     * deploying to production.
     *
     * @param http the HttpSecurity object provided by Spring
     * @return the configured SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            /* Disable CSRF for API endpoints called from HTML frontend */
            .csrf(csrf -> csrf.disable())

            /* Define access rules for each endpoint */
            .authorizeHttpRequests(auth -> auth
                /* Allow register and login without authentication */
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                /* All other endpoints require the user to be logged in */
                .anyRequest().authenticated()
            );

        return http.build();
    }
}