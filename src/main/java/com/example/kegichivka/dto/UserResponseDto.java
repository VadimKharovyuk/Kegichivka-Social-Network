package com.example.kegichivka.dto;

import com.example.kegichivka.enums.AccountStatus;
import com.example.kegichivka.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePhotoUrl;
    private UserRole role;
    private AccountStatus accountStatus;
}
