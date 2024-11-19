package com.example.kegichivka.service;

import com.example.kegichivka.enums.AccountStatus;
import com.example.kegichivka.model.BusinessUser;
import com.example.kegichivka.model.RegularUser;
import com.example.kegichivka.model.abstracts.BaseUser;
import com.example.kegichivka.repositoty.BusinessUserRepository;
import com.example.kegichivka.repositoty.RegularUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final RegularUserRepository regularUserRepository;
    private final BusinessUserRepository businessUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Сначала ищем в обычных пользователях
        Optional<RegularUser> regularUser = regularUserRepository.findByEmail(email);
        if (regularUser.isPresent()) {
            return (UserDetails) regularUser.get();
        }

        // Если не нашли, ищем в бизнес-пользователях
        return (UserDetails) businessUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public boolean isEmailTaken(String email) {
        return regularUserRepository.existsByEmail(email) ||
                businessUserRepository.existsByEmail(email);
    }


}
