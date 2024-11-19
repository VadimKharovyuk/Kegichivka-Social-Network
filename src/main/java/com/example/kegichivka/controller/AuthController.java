package com.example.kegichivka.controller;

import com.example.kegichivka.dto.JwtResponseDto;
import com.example.kegichivka.dto.LoginRequestDto;
import com.example.kegichivka.dto.RegisterRequestDto;
import com.example.kegichivka.enums.UserRole;
import com.example.kegichivka.exception.EmailAlreadyExistsException;
import com.example.kegichivka.exception.UsernameNotFoundException;
import com.example.kegichivka.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        log.debug("Showing registration form");
        model.addAttribute("registerRequest", new RegisterRequestDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequestDto request,
                           BindingResult result,
                           Model model) {
        log.debug("Processing registration for email: {}", request.getEmail());

        if (result.hasErrors()) {
            log.debug("Validation errors: {}", result.getAllErrors());
            return "auth/register";
        }

        try {
            JwtResponseDto response = authService.register(request);
            log.debug("Registration successful for email: {}", request.getEmail());
            model.addAttribute("email", request.getEmail());
            return "auth/registration-success";
        } catch (EmailAlreadyExistsException e) {
            log.debug("Registration failed: email already exists: {}", request.getEmail());
            model.addAttribute("emailError", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/activate")
    public String activateAccount(@RequestParam("token") String token, Model model) {
        log.debug("Processing account activation for token: {}", token);
        try {
            authService.activateAccount(token);
            log.debug("Account activation successful");
            model.addAttribute("message", "Аккаунт успешно активирован!");
            return "auth/activation-success";
        } catch (Exception e) {
            log.error("Account activation failed", e);
            model.addAttribute("error", "Ошибка активации: " + e.getMessage());
            return "auth/activation-error";
        }
    }

    @GetMapping("/resend-verification")
    public String resendVerification(@RequestParam String email, Model model) {
        try {
            authService.resendVerificationEmail(email);
            model.addAttribute("message", "Письмо с подтверждением отправлено повторно");
            return "auth/verification-resent";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка отправки письма: " + e.getMessage());
            return "auth/verification-error";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequestDto());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequestDto request,
                        BindingResult result,
                        Model model,
                        HttpSession session) {
        if (result.hasErrors()) {
            return "auth/login";
        }

        try {
            JwtResponseDto response = authService.login(request);
            // Сохраняем токены в сессии (или можно использовать куки)
            session.setAttribute("accessToken", response.getAccessToken());
            session.setAttribute("refreshToken", response.getRefreshToken());

            // Перенаправляем в зависимости от роли
            if (response.getUser().getRole() == UserRole.BUSINESS_USER) {
                return "redirect:/business/dashboard";
            } else if (response.getUser().getRole() == UserRole.ADMIN) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/user/dashboard";
            }
        } catch (UsernameNotFoundException e) {
            model.addAttribute("emailError", "Пользователь не найден");
            return "auth/login";
        } catch (BadCredentialsException e) {
            model.addAttribute("passwordError", "Неверный пароль");
            return "auth/login";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка входа: " + e.getMessage());
            return "auth/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}
