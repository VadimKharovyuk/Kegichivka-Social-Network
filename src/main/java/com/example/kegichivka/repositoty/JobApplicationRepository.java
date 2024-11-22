package com.example.kegichivka.repositoty;

import com.example.kegichivka.model.JobApplication;
import com.example.kegichivka.model.JobListing;
import com.example.kegichivka.model.RegularUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// 3. Репозиторий для откликов на вакансии
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    @Query("SELECT COUNT(a) FROM JobApplication a WHERE a.jobListing = :listing")
    Long countApplications(@Param("listing") JobListing listing);

    List<JobApplication> findByJobListing(JobListing jobListing);

    List<JobApplication> findByApplicant(RegularUser applicant);

    boolean existsByApplicantAndJobListing(RegularUser applicant, JobListing jobListing);
}
