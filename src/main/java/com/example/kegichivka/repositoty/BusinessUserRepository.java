package com.example.kegichivka.repositoty;

import com.example.kegichivka.enums.AccountStatus;
import com.example.kegichivka.enums.UserRole;
import com.example.kegichivka.enums.VerificationStatus;
import com.example.kegichivka.model.BusinessUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface BusinessUserRepository extends JpaRepository<BusinessUser, Long> {
    Optional<BusinessUser> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<BusinessUser> findByVerificationToken(String token);

    // Дополнительные методы
    List<BusinessUser> findByVerificationStatus(VerificationStatus status);
    List<BusinessUser> findByVerificationTokenExpiryBefore(LocalDateTime date);

    @Modifying
    @Query("UPDATE BusinessUser u SET u.verificationToken = null, u.verificationTokenExpiry = null " +
            "WHERE u.verificationTokenExpiry < :now AND u.verificationStatus = :status")
    void invalidateExpiredTokens(@Param("now") LocalDateTime now,
                                 @Param("status") VerificationStatus status);
}
