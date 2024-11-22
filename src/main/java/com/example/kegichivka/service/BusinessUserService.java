package com.example.kegichivka.service;

import com.example.kegichivka.exception.ResourceNotFoundException;
import com.example.kegichivka.model.BusinessUser;
import com.example.kegichivka.repositoty.BusinessUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusinessUserService {
    private final BusinessUserRepository businessUserRepository;

    public Optional<BusinessUser> getBusinessUserByEmail(String email) {
        return Optional.ofNullable(businessUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("BusinessUser not found for email: " + email)));
    }
}
