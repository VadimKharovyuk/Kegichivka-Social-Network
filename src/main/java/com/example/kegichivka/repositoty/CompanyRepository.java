package com.example.kegichivka.repositoty;

import com.example.kegichivka.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>,
        JpaSpecificationExecutor<Company> {

    boolean existsByTitle(String title);

    @Query("SELECT c FROM Company c WHERE c.verified = true")
    Page<Company> findAllVerified(Pageable pageable);
}
