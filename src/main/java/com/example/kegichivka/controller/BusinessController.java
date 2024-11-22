package com.example.kegichivka.controller;

import com.example.kegichivka.exception.ResourceNotFoundException;
import com.example.kegichivka.model.BusinessUser;
import com.example.kegichivka.service.BusinessUserService;
import com.example.kegichivka.service.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/business")
@RequiredArgsConstructor
public class BusinessController {
    private final BusinessUserService businessUserService;


    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        BusinessUser businessUser = businessUserService.getBusinessUserByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("BusinessUser not found"));
        model.addAttribute("user", businessUser);
        return "business/dashboard";
    }
}