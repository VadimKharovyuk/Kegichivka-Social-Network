package com.example.kegichivka.service;

import com.example.kegichivka.enums.AccountStatus;
import com.example.kegichivka.enums.UserRole;
import com.example.kegichivka.enums.VerificationStatus;
import com.example.kegichivka.model.Admin;
import com.example.kegichivka.model.abstracts.BaseUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class UserPrincipal implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private UserRole role;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean accountNonLocked;
    private boolean enabled;
    private String firstName;  // Добавляем поле
    private String lastName;   // Добавляем поле

    public static UserPrincipal build(BaseUser user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // Добавляем permissions для админа
        if (user instanceof Admin) {
            Admin admin = (Admin) user;
            admin.getPermissions().forEach(permission ->
                    authorities.add(new SimpleGrantedAuthority(permission))
            );
        }

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                authorities,
                user.getAccountStatus() == AccountStatus.ACTIVE,
                user.getVerificationStatus() == VerificationStatus.EMAIL_VERIFIED,
                user.getFirstName(),    // Добавляем поле
                user.getLastName()      // Добавляем поле
        );
    }

    // Специальный конструктор для всех полей
    public UserPrincipal(Long id, String email, String password, UserRole role,
                         Collection<? extends GrantedAuthority> authorities,
                         boolean accountNonLocked, boolean enabled,
                         String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
        this.accountNonLocked = accountNonLocked;
        this.enabled = enabled;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
//    private Long id;
//    private String email;
//    private String password;
//    private UserRole role;
//    private Collection<? extends GrantedAuthority> authorities;
//    private boolean accountNonLocked;
//    private boolean enabled;
//
//
//    public static UserPrincipal build(BaseUser user) {
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
//
//        // Добавляем permissions для админа
//        if (user instanceof Admin) {
//            Admin admin = (Admin) user;
//            admin.getPermissions().forEach(permission ->
//                    authorities.add(new SimpleGrantedAuthority(permission))
//            );
//        }
//
//        return new UserPrincipal(
//                user.getId(),
//                user.getEmail(),
//                user.getPassword(),
//                user.getRole(),
//                authorities,
//                user.getAccountStatus() == AccountStatus.ACTIVE,
//                user.getVerificationStatus() == VerificationStatus.EMAIL_VERIFIED
//        );
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return authorities;
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
}