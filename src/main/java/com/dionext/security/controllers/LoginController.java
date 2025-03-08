package com.dionext.security.controllers;

import com.dionext.ideaportal.services.CiteCreatorService;
import com.dionext.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @Autowired
    CiteCreatorService citeCreatorService;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @GetMapping("/login")
    public String login(Model model) {
        citeCreatorService.prepareTemplateModel(model);
        return "login"; // Return the login.html template
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        citeCreatorService.prepareTemplateModel(model);
        return "register"; // Отображение страницы регистрации
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, Model model) {
        userDetailsService.registerUser(username, password);
        citeCreatorService.prepareTemplateModel(model);
        model.addAttribute("message", "Регистрация прошла успешно!");
        return "redirect:/login"; // Перенаправление на страницу входа
    }
}