package com.example.kegichivka.repositoty;

import com.example.kegichivka.dto.ViewStatsSummaryDto;
import com.example.kegichivka.model.JobListing;
import com.example.kegichivka.model.JobViewStatistics;
import org.hibernate.stat.Statistics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ViewStatisticsRepository extends JpaRepository<JobViewStatistics, Long> {
    Optional<JobViewStatistics> findByJobListingId(Long jobListingId);

    @Query("SELECT vs FROM JobViewStatistics vs WHERE vs.jobListing.id = :jobId " +
            "AND KEY(vs.dailyViews) BETWEEN :startDate AND :endDate")
    Optional<JobViewStatistics> findStatisticsByJobIdAndDateRange(
            @Param("jobId") Long jobId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT vs.jobListing FROM JobViewStatistics vs " +
            "WHERE vs.jobListing.active = true " +
            "ORDER BY vs.viewCount DESC")
    List<JobListing> findMostViewedJobs(Pageable pageable);


}
