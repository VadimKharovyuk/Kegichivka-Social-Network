package com.example.kegichivka.controller;

import com.example.kegichivka.dto.JwtResponseDto;
import com.example.kegichivka.dto.LoginRequestDto;
import com.example.kegichivka.dto.RegisterRequestDto;
import com.example.kegichivka.enums.UserRole;
import com.example.kegichivka.exception.EmailAlreadyExistsException;
import com.example.kegichivka.exception.TokenExpiredException;
import com.example.kegichivka.exception.UsernameNotFoundException;
import com.example.kegichivka.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

    @GetMapping("/activation-success")
    public String showActivationSuccess() {
        return "auth/activation-success";
    }

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
    public String activateAccount(@RequestParam String token, Model model) {
        try {
            log.debug("Attempting to activate account with token: {}", token);
            authService.activateAccount(token);
            return "redirect:/auth/activation-success";
        } catch (TokenExpiredException e) {
            log.error("Token activation failed: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "auth/activation-error";
        } catch (IllegalStateException e) {
            log.error("Account activation failed: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "auth/activation-error";
        } catch (Exception e) {
            log.error("Unexpected error during activation: {}", e.getMessage());
            model.addAttribute("error", "Произошла ошибка при активации аккаунта");
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
                        HttpSession session,
                        HttpServletResponse response) {
        if (result.hasErrors()) {
            return "auth/login";
        }

        try {
            JwtResponseDto jwtResponse = authService.login(request);

            // Сохраняем токены в сессии
            session.setAttribute("accessToken", jwtResponse.getAccessToken());
            session.setAttribute("refreshToken", jwtResponse.getRefreshToken());

            // Создаем cookies для токенов
            Cookie accessTokenCookie = new Cookie("jwt_token", jwtResponse.getAccessToken());
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(3600); // 1 час
            response.addCookie(accessTokenCookie);

            // Добавляем токен в заголовок ответа
            response.setHeader("Authorization", "Bearer " + jwtResponse.getAccessToken());

            // Добавляем скрипт для сохранения токена в localStorage
            model.addAttribute("jwtToken", jwtResponse.getAccessToken());

            log.info("User {} successfully logged in", request.getEmail());

            // Перенаправляем в зависимости от роли
            return switch (jwtResponse.getUser().getRole()) {
                case BUSINESS_USER -> "redirect:/business/dashboard";
                case ADMIN -> "redirect:/admins/dashboard";
                default -> "redirect:/user/dashboard";
            };

        } catch (UsernameNotFoundException e) {
            log.warn("Login attempt failed: User not found - {}", request.getEmail());
            model.addAttribute("emailError", "Пользователь не найден");
            return "auth/login";
        } catch (BadCredentialsException e) {
            log.warn("Login attempt failed: Invalid password for user - {}", request.getEmail());
            model.addAttribute("passwordError", "Неверный пароль");
            return "auth/login";
        } catch (Exception e) {
            log.error("Login error", e);
            model.addAttribute("error", "Ошибка входа: " + e.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session,
                         HttpServletResponse response,
                         @CookieValue(name = "jwt_token", required = false) Cookie jwtCookie) {
        // Очищаем сессию
        session.invalidate();

        // Удаляем cookie
        if (jwtCookie != null) {
            jwtCookie.setMaxAge(0);
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);
        }

        return "redirect:/auth/login?logout";
    }
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}


//    @PostMapping("/login")
//    public String login(@Valid @ModelAttribute("loginRequest") LoginRequestDto request,
//                        BindingResult result,
//                        Model model,
//                        HttpSession session) {
//        if (result.hasErrors()) {
//            return "auth/login";
//        }
//
//        try {
//            JwtResponseDto response = authService.login(request);
//            // Сохраняем токены в сессии (или можно использовать куки)
//            session.setAttribute("accessToken", response.getAccessToken());
//            session.setAttribute("refreshToken", response.getRefreshToken());
//
//            // Перенаправляем в зависимости от роли
//            if (response.getUser().getRole() == UserRole.BUSINESS_USER) {
//                return "redirect:/business/dashboard";
//            } else if (response.getUser().getRole() == UserRole.ADMIN) {
//                return "redirect:/admins/dashboard";
//            } else {
//                return "redirect:/user/dashboard";
//            }
//        } catch (UsernameNotFoundException e) {
//            model.addAttribute("emailError", "Пользователь не найден");
//            return "auth/login";
//        } catch (BadCredentialsException e) {
//            model.addAttribute("passwordError", "Неверный пароль");
//            return "auth/login";
//        } catch (Exception e) {
//            model.addAttribute("error", "Ошибка входа: " + e.getMessage());
//            return "auth/login";
//        }
//    }



