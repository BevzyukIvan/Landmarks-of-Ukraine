package com.example.landmarks.security;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.landmarks.model.Role;
import com.example.landmarks.model.User;

@Data
public class RegistrationForm {
    private String username;

    @Size(min = 6, message = "Пароль повинен містити мінімум 6 символів")
    private String password;

    private String confirmPassword;

    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(username, passwordEncoder.encode(password), Role.ROLE_USER);
    }
}

