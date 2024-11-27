package com.example.kegichivka.repositoty;

import com.example.kegichivka.enums.ListingStatus;
import com.example.kegichivka.model.BusinessUser;
import com.example.kegichivka.model.Category;
import com.example.kegichivka.model.Company;
import com.example.kegichivka.model.JobListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface JobListingRepository extends JpaRepository<JobListing, Long>, JpaSpecificationExecutor<JobListing> {
    Page<JobListing> findByCategoryAndActiveTrue(Category category, Pageable pageable);

    Page<JobListing> findByActiveTrue(Pageable pageable);

    @Query("SELECT j FROM JobListing j WHERE j.active = true AND j.status = :status")
        // изменено с isActive на active
    Page<JobListing> findActiveJobListings(@Param("status") ListingStatus status, Pageable pageable);

    Page<JobListing> findByCompanyAndActiveTrue(Company company, Pageable pageable);

    @Query("SELECT j FROM JobListing j WHERE j.active = true AND j.salary BETWEEN :minSalary AND :maxSalary")
        // изменено с isActive на active
    Page<JobListing> findActiveBySalaryRange(
            @Param("minSalary") BigDecimal minSalary,
            @Param("maxSalary") BigDecimal maxSalary,
            Pageable pageable
    );

    @Query("SELECT j FROM JobListing j WHERE j.active = true AND LOWER(j.location.city) = LOWER(:city)")
        // изменено с isActive на active
    Page<JobListing> findActiveByCityIgnoreCase(@Param("city") String city, Pageable pageable);

    @Query("SELECT COUNT(j) FROM JobListing j WHERE j.active = true AND j.company.id = :companyId")
        // изменено с isActive на active
    Long countActiveByCompany(@Param("companyId") Long companyId);

    @Query("SELECT j FROM JobListing j WHERE j.active = true ORDER BY j.createdAt DESC")
        // изменено с isActive на active
    List<JobListing> findLatestActiveJobs(Pageable pageable);



    long countByCategoryAndActiveTrue(Category category);

    @Query("SELECT MIN(j.salary) FROM JobListing j " +
            "WHERE j.category = :category AND j.active = true AND j.salary IS NOT NULL")
    BigDecimal findMinSalaryByCategory(@Param("category") Category category);

    @Query("SELECT MAX(j.salary) FROM JobListing j " +
            "WHERE j.category = :category AND j.active = true AND j.salary IS NOT NULL")
    BigDecimal findMaxSalaryByCategory(@Param("category") Category category);

    List<JobListing> findByBusinessUserOrderByCreatedAtDesc(BusinessUser businessUser);

}
