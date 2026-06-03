package com.devhub.controller;

import com.devhub.dto.UserRegistrationDto;
import com.devhub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto dto, 
                               BindingResult result) {
        if (result.hasErrors()) return "register";
        
        try {
            userService.registerUser(dto);
            return "redirect:/login?success=true";
        } catch (Exception e) {
            result.rejectValue("username", "error.user", e.getMessage());
            return "register";
        }
    }
}