package com.example.landmarks.security;

import com.example.landmarks.model.Role;
import com.example.landmarks.model.User;
import com.example.landmarks.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/landmarks/new",
                                "/landmarks/*/edit",
                                "/landmarks/*/delete",
                                "/landmarks/*/comments",
                                "/landmarks/*/comments/*/delete",

                                "/users/*/edit",
                                "/users/*/follow",
                                "/users/*/unfollow"
                        ).authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminUsername = "admin";
            String adminPassword = "admin123";

            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                User admin = new User(adminUsername, passwordEncoder.encode(adminPassword), Role.ROLE_ADMIN);
                userRepository.save(admin);
            }
        };
    }

    @Bean
    public CommandLineRunner createTestUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            for (int i = 1; i <= 50; i++) {
                String username = "user" + i;
                if (userRepository.findByUsername(username).isEmpty()) {
                    User user = new User(username, passwordEncoder.encode("password123"), Role.ROLE_USER);
                    userRepository.save(user);
                }
            }
        };
    }
}
