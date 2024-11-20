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
@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipal implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private UserRole role;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean accountNonLocked;
    private boolean enabled;

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
                user.getVerificationStatus() == VerificationStatus.EMAIL_VERIFIED
        );
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
}
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class UserPrincipal implements UserDetails {
//    private Long id;
//    private String email;
//    private String password;
//    private UserRole role;
//    private Collection<? extends GrantedAuthority> authorities;
//
//    public static UserPrincipal build(BaseUser user) {
//        List<GrantedAuthority> authorities = Collections.singletonList(
//                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
//        );
//
//        return new UserPrincipal(
//                user.getId(),
//                user.getEmail(),
//                user.getPassword(),
//                user.getRole(),
//                authorities
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
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}
