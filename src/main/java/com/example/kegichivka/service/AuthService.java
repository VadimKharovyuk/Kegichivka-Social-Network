package com.example.kegichivka.service;

import com.example.kegichivka.config.JwtProperties;
import com.example.kegichivka.dto.JwtResponseDto;
import com.example.kegichivka.dto.LoginRequestDto;
import com.example.kegichivka.dto.RegisterRequestDto;
import com.example.kegichivka.enums.AccountStatus;
import com.example.kegichivka.enums.UserRole;
import com.example.kegichivka.enums.VerificationStatus;
import com.example.kegichivka.exception.EmailAlreadyExistsException;
import com.example.kegichivka.exception.TokenExpiredException;
import com.example.kegichivka.maper.UserMapper;
import com.example.kegichivka.model.Admin;
import com.example.kegichivka.model.BusinessUser;
import com.example.kegichivka.model.RegularUser;
import com.example.kegichivka.model.abstracts.BaseUser;

import com.example.kegichivka.repositoty.AdminRepository;
import com.example.kegichivka.repositoty.BusinessUserRepository;
import com.example.kegichivka.repositoty.RegularUserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserMapper userMapper;
    private final RegularUserRepository regularUserRepository;
    private final BusinessUserRepository businessUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final EmailService emailService;
    private final AdminRepository adminRepository;


    @Transactional
    public JwtResponseDto register(RegisterRequestDto request) {
        // Проверяем существование email
        if (regularUserRepository.existsByEmail(request.getEmail()) ||
                businessUserRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email уже зарегистрирован");
        }

        BaseUser user;
        if (request.getRole() == UserRole.BUSINESS_USER) {
            BusinessUser businessUser = userMapper.toBusinessUser(request);
            businessUser.setPassword(passwordEncoder.encode(request.getPassword()));
            user = businessUserRepository.save(businessUser);
        } else {
            RegularUser regularUser = userMapper.toRegularUser(request);
            regularUser.setPassword(passwordEncoder.encode(request.getPassword()));
            user = regularUserRepository.save(regularUser);
        }

        emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return JwtResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toDto(user))
                .expiresIn(jwtProperties.getAccessTokenExpiration())
                .build();
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        BaseUser user = regularUserRepository.findByEmail(email)
                .map(u -> (BaseUser) u)
                .orElseGet(() -> businessUserRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found")));

        if (user.getVerificationStatus() != VerificationStatus.UNVERIFIED) {
            throw new IllegalStateException("User is already verified or in verification process");
        }

        // Обновляем токен и время его действия
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

        if (user instanceof RegularUser) {
            regularUserRepository.save((RegularUser) user);
        } else {
            businessUserRepository.save((BusinessUser) user);
        }

        emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());
    }
    public JwtResponseDto login(LoginRequestDto request) {
        log.debug("Attempting login for user: {}", request.getEmail());

        // Находим пользователя во всех репозиториях
        BaseUser user = adminRepository.findByEmail(request.getEmail())
                .map(admin -> (BaseUser) admin)
                .orElseGet(() -> regularUserRepository.findByEmail(request.getEmail())
                        .map(regularUser -> (BaseUser) regularUser)
                        .orElseGet(() -> businessUserRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> {
                                    log.warn("User not found: {}", request.getEmail());
                                    return new UsernameNotFoundException("Пользователь не найден");
                                })));

        // Проверяем пароль
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", request.getEmail());
            throw new BadCredentialsException("Неверный пароль");
        }

        // Проверяем статус аккаунта
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            log.warn("Account not activated for user: {}", request.getEmail());
            throw new BadCredentialsException("Аккаунт не активирован");
        }

        if (user.getVerificationStatus() != VerificationStatus.EMAIL_VERIFIED) {
            log.warn("Email not verified for user: {}", request.getEmail());
            throw new BadCredentialsException("Email не подтвержден");
        }

        // Дополнительные проверки для админа
        if (user instanceof Admin) {
            Admin admin = (Admin) user;
            if (!admin.isAccountNonLocked()) {
                log.warn("Admin account is locked: {}", request.getEmail());
                throw new BadCredentialsException("Аккаунт заблокирован");
            }
            // Обновляем дату последней активности
            admin.setLastActiveDate(LocalDateTime.now());
            adminRepository.save(admin);
        }

        // Генерируем токены
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("Successfully logged in user: {}", request.getEmail());

        return JwtResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toDto(user))
                .expiresIn(jwtProperties.getAccessTokenExpiration())
                .build();
    }



    @Transactional
    public void activateAccount(String token) {
        log.debug("Attempting to activate account with token: {}", token);

        // Сначала ищем среди админов, так как это более специфичный случай
        BaseUser user = adminRepository.findByVerificationToken(token)
                .map(admin -> (BaseUser) admin)
                .orElseGet(() ->
                        // Если админ не найден, ищем среди обычных пользователей
                        regularUserRepository.findByVerificationToken(token)
                                .map(regularUser -> (BaseUser) regularUser)
                                .orElseGet(() ->
                                        // Если обычный пользователь не найден, ищем среди бизнес-пользователей
                                        businessUserRepository.findByVerificationToken(token)
                                                .map(businessUser -> (BaseUser) businessUser)
                                                .orElseThrow(() -> {
                                                    log.error("No user found with verification token: {}", token);
                                                    return new TokenExpiredException("Неверный токен активации");
                                                })
                                )
                );

        log.debug("Found user with email: {} and role: {}", user.getEmail(), user.getRole());

        // Проверяем срок действия токена
        if (user.getVerificationTokenExpiry() == null ||
                user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            log.warn("Token expired for user: {}", user.getEmail());
            throw new TokenExpiredException("Срок действия токена истек");
        }

        // Проверяем текущий статус верификации
        if (user.getVerificationStatus() == VerificationStatus.EMAIL_VERIFIED) {
            log.warn("User already verified: {}", user.getEmail());
            throw new IllegalStateException("Аккаунт уже верифицирован");
        }

        try {
            // Обновляем статусы
            user.setVerificationStatus(VerificationStatus.EMAIL_VERIFIED);
            user.setAccountStatus(AccountStatus.ACTIVE);
            user.setVerificationToken("");
            user.setVerificationTokenExpiry(null);

            // Сохраняем изменения в соответствующем репозитории
            if (user instanceof Admin) {
                Admin admin = (Admin) user;
                admin.setRegistrationDate(LocalDateTime.now());
                admin.setLastActiveDate(LocalDateTime.now());
                adminRepository.save(admin);
                log.info("Activated admin account: {}", user.getEmail());
            } else if (user instanceof RegularUser) {
                regularUserRepository.save((RegularUser) user);
                log.info("Activated regular user account: {}", user.getEmail());
            } else if (user instanceof BusinessUser) {
                businessUserRepository.save((BusinessUser) user);
                log.info("Activated business user account: {}", user.getEmail());
            }
        } catch (Exception e) {
            log.error("Error during account activation for user: {}", user.getEmail(), e);
            throw new RuntimeException("Ошибка при активации аккаунта", e);
        }
    }

    public boolean isEmailExists(String email) {
        return regularUserRepository.existsByEmail(email) ||
                businessUserRepository.existsByEmail(email) ||
                adminRepository.existsByEmail(email);
    }
}
