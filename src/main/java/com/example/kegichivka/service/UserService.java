package com.example.kegichivka.service;//package com.example.kegichivka.service;
//
//import com.example.kegichivka.enums.AccountStatus;
//import com.example.kegichivka.enums.VerificationStatus;
//import com.example.kegichivka.model.Admin;
//import com.example.kegichivka.model.BusinessUser;
//import com.example.kegichivka.model.RegularUser;
//import com.example.kegichivka.model.abstracts.BaseUser;
//import com.example.kegichivka.repositoty.AdminRepository;
//import com.example.kegichivka.repositoty.BusinessUserRepository;
//import com.example.kegichivka.repositoty.RegularUserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class UserService implements UserDetailsService {
//    private final RegularUserRepository regularUserRepository;
//    private final BusinessUserRepository businessUserRepository;
//    private final AdminRepository adminRepository;
//
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        // Сначала ищем админа
//        return adminRepository.findByEmail(email)
//                .map(this::createUserDetails)
//                .orElseGet(() ->
//                        // Затем обычного пользователя
//                        regularUserRepository.findByEmail(email)
//                                .map(this::createUserDetails)
//                                .orElseGet(() ->
//                                        // Наконец, бизнес-пользователя
//                                        businessUserRepository.findByEmail(email)
//                                                .map(this::createUserDetails)
//                                                .orElseThrow(() ->
//                                                        new UsernameNotFoundException("User not found with email: " + email)
//                                                )
//                                )
//                );
//    }
//    private UserDetails createUserDetails(BaseUser user) {
//        Collection<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
//
//        // Добавляем дополнительные permissions для админа
//        if (user instanceof Admin) {
//            Admin admin = (Admin) user;
//            admin.getPermissions().forEach(permission ->
//                    authorities.add(new SimpleGrantedAuthority(permission))
//            );
//        }
//
//        return new User(
//                user.getEmail(),
//                user.getPassword(),
//                user.getVerificationStatus() == VerificationStatus.EMAIL_VERIFIED,  // enabled
//                true,  // accountNonExpired
//                true,  // credentialsNonExpired
//                user.getAccountStatus() == AccountStatus.ACTIVE,  // accountNonLocked
//                authorities
//        );
//    }
//
//    public boolean isEmailTaken(String email) {
//        return regularUserRepository.existsByEmail(email) ||
//                businessUserRepository.existsByEmail(email);
//    }
//
//
//}

import com.example.kegichivka.exception.UsernameNotFoundException;
import com.example.kegichivka.repositoty.AdminRepository;
import com.example.kegichivka.repositoty.BusinessUserRepository;
import com.example.kegichivka.repositoty.RegularUserRepository;
import com.example.kegichivka.service.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final RegularUserRepository regularUserRepository;
    private final BusinessUserRepository businessUserRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return adminRepository.findByEmail(email)
                .map(UserPrincipal::build)
                .orElseGet(() ->
                        regularUserRepository.findByEmail(email)
                                .map(UserPrincipal::build)
                                .orElseGet(() ->
                                        businessUserRepository.findByEmail(email)
                                                .map(UserPrincipal::build)
                                                .orElseThrow(() ->
                                                        new UsernameNotFoundException("User not found with email: " + email)
                                                )
                                )
                );
    }
}
