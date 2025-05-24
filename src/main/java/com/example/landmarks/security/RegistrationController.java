package com.example.landmarks.security;

import com.example.landmarks.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "security/registration";
    }

    @PostMapping
    public String processRegistration(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                                      BindingResult bindingResult) {
        if (!form.isPasswordConfirmed()) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Паролі не співпадають!");
        }

        if (userService.usernameExists(form.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Користувач з таким ім’ям вже існує!");
        }

        if (bindingResult.hasErrors()) {
            return "security/registration";
        }

        userService.register(form.toUser(passwordEncoder));
        return "redirect:/login";
    }
}