package com.example.kegichivka.controller;

import com.example.kegichivka.dto.resume.ResumeDto;
import com.example.kegichivka.model.BusinessUser;
import com.example.kegichivka.model.RegularUser;
import com.example.kegichivka.service.ResumeService;
import com.example.kegichivka.service.UserPrincipal;
import com.example.kegichivka.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final ResumeService resumeService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserPrincipal currentUser) {
        // Получаем резюме пользователя
        List<ResumeDto> userResumes = resumeService.getCurrentUserResumes(currentUser.getId());

        // Добавляем в модель
        model.addAttribute("resumes", userResumes);
        model.addAttribute("user", currentUser);  // Передаем UserPrincipal

        return "user/dashboard";
    }
}


