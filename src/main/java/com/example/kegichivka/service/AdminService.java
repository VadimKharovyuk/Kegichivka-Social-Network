package com.example.kegichivka.service;

import com.example.kegichivka.dto.admin.AdminResponse;
import com.example.kegichivka.dto.admin.CreateAdminRequest;
import com.example.kegichivka.dto.admin.UpdateAdminRequest;
import com.example.kegichivka.enums.AccountStatus;
import com.example.kegichivka.enums.UserRole;
import com.example.kegichivka.enums.VerificationStatus;
import com.example.kegichivka.exception.*;
import com.example.kegichivka.maper.admin.AdminMapper;
import com.example.kegichivka.model.Admin;
import com.example.kegichivka.repositoty.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final ImgurService imgurService;
    private final EmailService emailService;
    private final AuthService authService;

    public AdminResponse createAdmin(CreateAdminRequest request) {
        log.debug("Creating new admin with email: {}", request.getEmail());

        if (authService.isEmailExists(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new EmailAlreadyExistsException("Email already registered");
        }

        try {
            Admin admin = adminMapper.toEntity(request);

            // Логируем состояние объекта после маппинга
            log.debug("Admin after mapping: {}", admin);

            admin.setPassword(passwordEncoder.encode(request.getPassword()));
            admin.setRole(UserRole.ADMIN);

            // Генерация токена верификации
            String verificationToken = UUID.randomUUID().toString();
            log.debug("Generated verification token: {}", verificationToken);

            admin.setVerificationToken(verificationToken);
            admin.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
            admin.setVerificationStatus(VerificationStatus.UNVERIFIED);
            admin.setAccountStatus(AccountStatus.PENDING_ACTIVATION);

            // Логируем состояние объекта перед сохранением
            log.debug("Admin before saving: verification_token={}, token_expiry={}, status={}",
                    admin.getVerificationToken(),
                    admin.getVerificationTokenExpiry(),
                    admin.getVerificationStatus());

            Admin savedAdmin = adminRepository.save(admin);

            // Логируем состояние объекта после сохранения
            log.debug("Saved admin: id={}, verification_token={}, token_expiry={}",
                    savedAdmin.getId(),
                    savedAdmin.getVerificationToken(),
                    savedAdmin.getVerificationTokenExpiry());

            // Отправка email с токеном активации для админа
            emailService.sendAdminActivationEmail(
                    savedAdmin.getEmail(),
                    verificationToken,
                    savedAdmin.getFirstName()
            );

            // Проверяем, что токен действительно сохранился
            Admin verifyAdmin = adminRepository.findByVerificationToken(verificationToken)
                    .orElse(null);
            if (verifyAdmin == null) {
                log.error("Admin not found by token immediately after saving!");
            } else {
                log.debug("Successfully verified admin exists with token");
            }

            return adminMapper.toResponse(savedAdmin);
        } catch (Exception e) {
            log.error("Error creating admin", e);
            throw new RuntimeException("Failed to create admin", e);
        }
    }

    @Transactional
    public void activateAdminAccount(String token) {
        log.debug("Activating admin account with token: {}", token);

        Admin admin = adminRepository.findByVerificationToken(token)
                .orElseThrow(() -> {
                    log.warn("No admin found with token: {}", token);
                    return new TokenExpiredException("Invalid activation token");
                });

        if (admin.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            log.warn("Token expired for admin: {}", admin.getEmail());
            throw new TokenExpiredException("Token has expired");
        }

        if (admin.getVerificationStatus() == VerificationStatus.EMAIL_VERIFIED) {
            log.warn("Admin already verified: {}", admin.getEmail());
            throw new IllegalStateException("Admin account is already verified");
        }

        admin.setVerificationStatus(VerificationStatus.EMAIL_VERIFIED);
        admin.setAccountStatus(AccountStatus.ACTIVE);
        admin.setVerificationToken("");
        admin.setVerificationTokenExpiry(null);
        admin.setRegistrationDate(LocalDateTime.now());
        admin.setLastActiveDate(LocalDateTime.now());

        adminRepository.save(admin);
        log.info("Successfully activated admin account: {}", admin.getEmail());
    }

    public void resendVerificationEmail(String email) {
        log.debug("Resending verification email to admin: {}", email);

        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("No admin found with email: {}", email);
                    return new ResourceNotFoundException("Admin not found");
                });

        if (admin.getVerificationStatus() == VerificationStatus.EMAIL_VERIFIED) {
            log.warn("Admin already verified: {}", email);
            throw new IllegalStateException("Admin is already verified");
        }

        // Генерация нового токена
        String newToken = UUID.randomUUID().toString();
        admin.setVerificationToken(newToken);
        admin.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        adminRepository.save(admin);

        // Отправка нового email для активации
        emailService.sendAdminActivationEmail(
                admin.getEmail(),
                newToken,
                admin.getFirstName()
        );
        log.info("Successfully resent verification email to admin: {}", email);
    }

    public AdminResponse updateAdmin(Long id, UpdateAdminRequest request) {
        return adminRepository.findById(id)
                .map(admin -> {
                    adminMapper.updateAdminFromDto(request, admin);
                    Admin updatedAdmin = adminRepository.save(admin);
                    log.debug("Updated admin with id: {}", id);
                    return adminMapper.toResponse(updatedAdmin);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
    }

    public AdminResponse getAdminById(Long id) {
        return adminRepository.findById(id)
                .map(adminMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
    }

    public AdminResponse updateProfilePhoto(Long id, byte[] photoData) {
        return adminRepository.findById(id)
                .map(admin -> {
                    String photoUrl = imgurService.uploadImage(photoData);
                    if (photoUrl == null) {
                        throw new RuntimeException("Failed to upload profile photo");
                    }
                    admin.setProfilePhotoUrl(photoUrl);
                    Admin updatedAdmin = adminRepository.save(admin);
                    log.debug("Updated profile photo for admin id: {}", id);
                    return adminMapper.toResponse(updatedAdmin);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
    }

    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new InvalidPasswordException("Invalid old password");
        }

        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
        log.debug("Updated password for admin id: {}", id);
    }

    public void toggleEmailNotifications(Long id) {
        adminRepository.findById(id)
                .map(admin -> {
                    admin.setEmailNotificationsEnabled(!admin.isEmailNotificationsEnabled());
                    adminRepository.save(admin);
                    log.debug("Toggled email notifications for admin id: {}", id);
                    return admin;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
    }

    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new ResourceNotFoundException("Admin not found");
        }
        adminRepository.deleteById(id);
        log.debug("Deleted admin with id: {}", id);
    }





}