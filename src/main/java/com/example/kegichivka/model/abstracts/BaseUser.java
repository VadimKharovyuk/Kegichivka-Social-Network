package com.example.kegichivka.model.abstracts;

import com.example.kegichivka.enums.AccountStatus;
import com.example.kegichivka.enums.UserRole;
import com.example.kegichivka.enums.VerificationStatus;
import com.example.kegichivka.model.Notification;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String phoneNumber;


    @Column(nullable = false)
    private LocalDate dateOfBirth;
    @Column
    private String profilePhotoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus = AccountStatus.PENDING_ACTIVATION;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @Column(nullable = false)
    private LocalDateTime lastActiveDate;

    @Column(nullable = false)
    private String verificationToken;

    @Column
    private LocalDateTime verificationTokenExpiry;

    @Column(nullable = false)
    private boolean emailNotificationsEnabled = true;

    @Column(nullable = false)
    private boolean accountNonLocked = true;

    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
        lastActiveDate = LocalDateTime.now();
        verificationToken = UUID.randomUUID().toString();
        verificationTokenExpiry = LocalDateTime.now().plusHours(24);
    }


}