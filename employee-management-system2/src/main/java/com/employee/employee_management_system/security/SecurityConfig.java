package com.employee.employee_management_system.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    // Define constants for repeated literals
    private static final String EMPLOYEES_API = "/api/employees/**";


    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        // In-memory user and admin creation
        UserDetails user = User.builder()
                .username("user")
                .password("{noop}test123")  // No-op encoder
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}test123")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> configurer
                        // Allow GET requests for /api/employees/** for USER role
                        .requestMatchers(HttpMethod.GET, EMPLOYEES_API).hasRole("USER")
                        // Allow POST, PUT for USER role
                        .requestMatchers(HttpMethod.POST, "/api/employees").hasAnyRole("USER" , "ADMIN")
                        .requestMatchers(HttpMethod.PUT, EMPLOYEES_API).hasAnyRole("USER" , "ADMIN")
                        // Allow DELETE for ADMIN role only
                        .requestMatchers(HttpMethod.DELETE, EMPLOYEES_API).hasRole("ADMIN")
                )
                // Use HTTP Basic Authentication
                .httpBasic(Customizer.withDefaults())
                // Disable Cross Site Request Forgery (CSRF)
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
