package com.example.kegichivka.repositoty;

import com.example.kegichivka.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    //lazy fix
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByVerificationToken(String token);
    boolean existsByEmail(String email);


}
