package com.example.kegichivka.dto.admin;

import com.example.kegichivka.enums.AccountStatus;
import com.example.kegichivka.enums.UserRole;
import com.example.kegichivka.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String profilePhotoUrl;
    private UserRole role;
    private Set<String> permissions;
    private VerificationStatus verificationStatus;
    private AccountStatus accountStatus;
    private LocalDateTime registrationDate;
    private LocalDateTime lastActiveDate;
    private boolean emailNotificationsEnabled;
    private boolean accountNonLocked;
}
