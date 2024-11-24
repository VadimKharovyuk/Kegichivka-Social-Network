package com.example.kegichivka.controller;

import com.example.kegichivka.dto.JwtResponseDto;
import com.example.kegichivka.dto.LoginRequestDto;
import com.example.kegichivka.dto.admin.AdminDashboardDto;
import com.example.kegichivka.dto.admin.AdminResponse;
import com.example.kegichivka.dto.admin.CreateAdminRequest;
import com.example.kegichivka.enums.AccountStatus;
import com.example.kegichivka.enums.UserRole;
import com.example.kegichivka.exception.EmailAlreadyExistsException;
import com.example.kegichivka.exception.ResourceNotFoundException;
import com.example.kegichivka.exception.TokenExpiredException;
import com.example.kegichivka.exception.UsernameNotFoundException;
import com.example.kegichivka.model.Admin;
import com.example.kegichivka.model.RegularUser;
import com.example.kegichivka.repositoty.AdminRepository;
import com.example.kegichivka.service.AdminService;
import com.example.kegichivka.service.AuthService;
import com.example.kegichivka.service.JwtService;
import com.example.kegichivka.service.UserPrincipal;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AuthService authService;
    private final AdminRepository adminRepository;
    private final JwtService jwtService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            // Получаем email текущего авторизованного пользователя
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            AdminDashboardDto dashboardData = adminService.loadAdminDashboard(email);

            model.addAttribute("dashboard", dashboardData);
            model.addAttribute("admin", dashboardData.getAdmin());

            return "admin/dashboard";

        } catch (UsernameNotFoundException e) {
            log.error("Admin not found for dashboard", e);
            model.addAttribute("error", "Админ с указанным email не найден.");
            return "error/error";
        } catch (Exception e) {
            log.error("Error loading dashboard", e);
            model.addAttribute("error", "Ошибка при загрузке дашборда.");
            return "error/error";
        }
    }


    @GetMapping("/activate")
    public String activateAccount(@RequestParam String token, Model model) {
        log.debug("Received activation request with token: {}", token);

        // Попробуйте найти токен во всех репозиториях
        adminRepository.findByVerificationToken(token)
                .ifPresentOrElse(
                        admin -> log.debug("Found admin with token"),
                        () -> log.debug("Admin not found with token")
                );

        try {
            authService.activateAccount(token);
            return "redirect:/auth/activation-success";
        } catch (TokenExpiredException e) {
            log.error("Token activation failed: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "auth/activation-error";
        }
    }



    @GetMapping("/activation-error")
    public String showActivationError() {
        return "admin/activation-error";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("createAdminRequest", new CreateAdminRequest());
        return "admin/register";
    }

    @PostMapping("/register")
    public String registerAdmin(@Valid @ModelAttribute CreateAdminRequest request,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            return "admin/register";
        }

        try {
            adminService.createAdmin(request);
            return "redirect:/admins/registration-success";
        } catch (EmailAlreadyExistsException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/register";
        }
    }


    @GetMapping("/registration-success")
    public String showRegistrationSuccess() {
        return "admin/registration-success";
    }

    @GetMapping("/verification-sent")
    public String showVerificationSentPage() {
        return "admin/verification-sent";
    }



    @GetMapping("/resend-verification")
    public String showResendVerificationForm() {
        return "admin/resend-verification";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam String email) {
        adminService.resendVerificationEmail(email);
        return "redirect:/admins/verification-sent";
    }
}
