package com.example.kegichivka.controller;

import com.example.kegichivka.model.BusinessUser;
import com.example.kegichivka.model.RegularUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal RegularUser user) {
        model.addAttribute("user", user);
        return "user/dashboard";
    }
}


