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
import com.example.kegichivka.model.BusinessUser;
import com.example.kegichivka.model.RegularUser;
import com.example.kegichivka.model.abstracts.BaseUser;

import com.example.kegichivka.repositoty.BusinessUserRepository;
import com.example.kegichivka.repositoty.RegularUserRepository;
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
        // Находим пользователя в обоих репозиториях
        BaseUser user = regularUserRepository.findByEmail(request.getEmail())
                .map(u -> (BaseUser) u)
                .orElseGet(() -> businessUserRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден")));

        // Проверяем пароль
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Неверный пароль");
        }

        // Проверяем статус аккаунта
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadCredentialsException("Аккаунт не активирован");
        }

        // Генерируем токены
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
    public void activateAccount(String token) {
        BaseUser user = regularUserRepository.findByVerificationToken(token)
                .map(regularUser -> (BaseUser) regularUser)  // изменили имя параметра
                .orElseGet(() -> businessUserRepository.findByVerificationToken(token)
                        .orElseThrow(() -> new TokenExpiredException("Неверный токен активации")));

        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Срок действия токена истек");
        }

        user.setVerificationStatus(VerificationStatus.EMAIL_VERIFIED);
        user.setAccountStatus(AccountStatus.ACTIVE);

        if (user instanceof RegularUser) {
            regularUserRepository.save((RegularUser) user);
        } else {
            businessUserRepository.save((BusinessUser) user);
        }
    }
}
