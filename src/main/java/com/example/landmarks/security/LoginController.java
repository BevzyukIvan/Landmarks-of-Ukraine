package com.example.landmarks.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            RedirectAttributes redirectAttributes) {
        if (error != null) {
            redirectAttributes.addFlashAttribute("loginError", "Неправильний логін або пароль!");
            return "redirect:/login";
        }
        return "security/login";
    }

    @PostMapping
    public String processLogin() {
        return "redirect:/landmarks"; // Spring Security обробляє вхід сам
    }
}
