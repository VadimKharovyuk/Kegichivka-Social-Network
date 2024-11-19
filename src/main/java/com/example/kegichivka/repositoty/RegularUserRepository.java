package com.example.kegichivka.repositoty;


import com.example.kegichivka.enums.AccountStatus;
import com.example.kegichivka.enums.UserRole;
import com.example.kegichivka.enums.VerificationStatus;
import com.example.kegichivka.model.RegularUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegularUserRepository extends JpaRepository<RegularUser, Long> {
    Optional<RegularUser> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<RegularUser> findByVerificationToken(String token);

    // Дополнительные методы
    List<RegularUser> findByVerificationStatus(VerificationStatus status);
    List<RegularUser> findByVerificationTokenExpiryBefore(LocalDateTime date);

    // Для очистки просроченных токенов
    @Modifying
    @Query("UPDATE RegularUser u SET u.verificationToken = null, u.verificationTokenExpiry = null " +
            "WHERE u.verificationTokenExpiry < :now AND u.verificationStatus = :status")
    void invalidateExpiredTokens(@Param("now") LocalDateTime now,
                                 @Param("status") VerificationStatus status);
}