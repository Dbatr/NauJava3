package ru.denis.NauJava3.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.denis.NauJava3.dto.UserRequest;
import ru.denis.NauJava3.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Показывает страницу входа
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * Показывает форму регистрации
     */
    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRequest", new UserRequest());
        return "registration";
    }

    /**
     * Обрабатывает отправку формы регистрации
     */
    @PostMapping("/registration")
    public String registerUser(@ModelAttribute @Valid UserRequest userRequest,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        try {

            userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            userService.addUser(userRequest);
            return "redirect:/login?registered";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "registration";
        }
    }

    /**
     * Показывает главную страницу
     */
    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }
}
