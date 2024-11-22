package com.example.kegichivka.service;

import com.example.kegichivka.dto.*;
import com.example.kegichivka.enums.ApplicationStatus;
import com.example.kegichivka.enums.ListingStatus;
import com.example.kegichivka.exception.ResourceNotFoundException;
import com.example.kegichivka.maper.JobListingMapper;
import com.example.kegichivka.model.*;
import com.example.kegichivka.repositoty.CategoryRepository;
import com.example.kegichivka.repositoty.JobApplicationRepository;
import com.example.kegichivka.repositoty.JobListingRepository;
import com.example.kegichivka.repositoty.ViewStatisticsRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobListingService {
    private final JobListingRepository jobListingRepository;
    private final JobListingMapper jobListingMapper;
    private final CategoryRepository categoryRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ViewStatisticsRepository viewStatisticsRepository;

    public JobListingResponseDto createJob(CreateJobListingDto dto, BusinessUser businessUser) {
        log.info("Starting job creation process");

        if (businessUser == null) {
            log.error("BusinessUser is null");
            throw new IllegalArgumentException("BusinessUser cannot be null");
        }

        log.debug("Creating job listing entity from DTO");
        JobListing jobListing = jobListingMapper.toEntity(dto, businessUser);
        jobListing.setBusinessUser(businessUser);

        LocalDateTime now = LocalDateTime.now();
        jobListing.setCreatedAt(now);
        jobListing.setUpdatedAt(now);
        jobListing.setVersion(0L);
        jobListing.setActive(true);

        if (dto.getCategoryId() != null) {
            log.debug("Finding category with id: {}", dto.getCategoryId());
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> {
                        log.error("Category not found with id: {}", dto.getCategoryId());
                        return new ResourceNotFoundException("Category not found");
                    });
            jobListing.setCategory(category);
        }

        log.debug("Saving job listing to database");
        jobListing = jobListingRepository.save(jobListing);
        log.info("Successfully saved job listing with id: {}", jobListing.getId());

        return jobListingMapper.toResponseDto(jobListing);
    }

    @Transactional(readOnly = true)
    public Page<JobListingShortDto> getJobsByCategory(Category category, Pageable pageable) {
        return jobListingRepository.findByCategoryAndActiveTrue(category, pageable)
                .map(jobListingMapper::toShortDto);
    }

    @Transactional(readOnly = true)
    public Page<JobListingShortDto> getActiveJobs(Pageable pageable) {
        return jobListingRepository.findByActiveTrue(pageable)
                .map(jobListingMapper::toShortDto);
    }

    @Transactional(readOnly = true)
    public Page<JobListingShortDto> getJobsByCompany(Company company, Pageable pageable) {
        return jobListingRepository.findByCompanyAndActiveTrue(company, pageable)
                .map(jobListingMapper::toShortDto);
    }

    @Transactional(readOnly = true)
    public Page<JobListingShortDto> getJobsBySalaryRange(
            BigDecimal minSalary,
            BigDecimal maxSalary,
            Pageable pageable) {
        return jobListingRepository.findActiveBySalaryRange(minSalary, maxSalary, pageable)
                .map(jobListingMapper::toShortDto);
    }

    @Transactional(readOnly = true)
    public Page<JobListingShortDto> getJobsByCity(String city, Pageable pageable) {
        return jobListingRepository.findActiveByCityIgnoreCase(city, pageable)
                .map(jobListingMapper::toShortDto);
    }

    @Transactional(readOnly = true)
    public Page<JobListingShortDto> searchJobs(JobSearchDto searchDto, Pageable pageable) {
        Specification<JobListing> spec = createSearchSpecification(searchDto);
        Page<JobListing> jobListings = jobListingRepository.findAll(spec, pageable);
        return jobListings.map(jobListingMapper::toShortDto);
    }

    private Specification<JobListing> createSearchSpecification(JobSearchDto searchDto) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("active"), true));
            predicates.add(cb.equal(root.get("status"), ListingStatus.APPROVED));

            if (StringUtils.hasText(searchDto.getKeyword())) {
                String keyword = "%" + searchDto.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), keyword),
                        cb.like(cb.lower(root.get("description")), keyword),
                        cb.like(cb.lower(root.get("position")), keyword)
                ));
            }

            if (searchDto.getMinSalary() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("salary"), searchDto.getMinSalary()));
            }
            if (searchDto.getMaxSalary() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("salary"), searchDto.getMaxSalary()));
            }

            if (!CollectionUtils.isEmpty(searchDto.getEmploymentTypes())) {
                predicates.add(root.get("employmentType").in(searchDto.getEmploymentTypes()));
            }

            if (!CollectionUtils.isEmpty(searchDto.getExperienceLevels())) {
                predicates.add(root.get("requiredExperience").in(searchDto.getExperienceLevels()));
            }

            if (searchDto.getIsRemote() != null) {
                predicates.add(cb.equal(root.get("remote"), searchDto.getIsRemote()));
            }

            if (!CollectionUtils.isEmpty(searchDto.getCategoryIds())) {
                predicates.add(root.get("category").get("id").in(searchDto.getCategoryIds()));
            }

            if (!CollectionUtils.isEmpty(searchDto.getCompanyIds())) {
                predicates.add(root.get("company").get("id").in(searchDto.getCompanyIds()));
            }

            if (searchDto.getPostedAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), searchDto.getPostedAfter()));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }


    @Transactional
    public void incrementViewCount(JobListing jobListing) {
        JobViewStatistics stats = jobListing.getOrCreateViewStatistics();
        stats.incrementViewCount();
        viewStatisticsRepository.save(stats);
    }


    @Transactional(readOnly = true)
    public Map<LocalDate, Integer> getViewStatistics(Long jobId, LocalDate startDate, LocalDate endDate) {
        return viewStatisticsRepository
                .findStatisticsByJobIdAndDateRange(jobId, startDate, endDate)
                .map(JobViewStatistics::getDailyViews)
                .orElse(new HashMap<>());
    }

    @Transactional
    public JobListingResponseDto updateJob(Long id, UpdateJobListingDto dto, BusinessUser businessUser)
            throws AccessDeniedException {
        JobListing jobListing = findJobListingById(id);
        validateJobOwnership(jobListing, businessUser);

        jobListingMapper.updateEntityFromDto(dto, jobListing);
        jobListing = jobListingRepository.save(jobListing);

        return jobListingMapper.toResponseDto(jobListing);
    }

    @Transactional
    public void deleteJob(Long id, BusinessUser businessUser) throws AccessDeniedException {
        JobListing jobListing = findJobListingById(id);
        validateJobOwnership(jobListing, businessUser);
        jobListingRepository.delete(jobListing);
    }

    public JobListing findJobListingById(Long id) {
        return jobListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job listing not found"));
    }

    // Метод canApply остается без изменений
    public boolean canApply(RegularUser user, JobListing jobListing) {
        return !jobApplicationRepository.existsByApplicantAndJobListing(user, jobListing);
    }



    private void validateJobOwnership(JobListing jobListing, BusinessUser businessUser)
            throws AccessDeniedException {
        if (!jobListing.getBusinessUser().getId().equals(businessUser.getId())) {
            throw new AccessDeniedException("You don't have permission to modify this job listing");
        }
    }

    private void validateCreateJobDto(CreateJobListingDto dto) {
        if (dto.getMinYearsExperience() != null && dto.getMaxYearsExperience() != null
                && dto.getMinYearsExperience() > dto.getMaxYearsExperience()) {
            throw new ValidationException("Minimum years of experience cannot be greater than maximum");
        }
    }

    private Map<ApplicationStatus, Long> convertApplicationStats(List<Object[]> stats) {
        Map<ApplicationStatus, Long> result = new EnumMap<>(ApplicationStatus.class);
        for (Object[] stat : stats) {
            ApplicationStatus status = (ApplicationStatus) stat[0];
            Long count = (Long) stat[1];
            result.put(status, count);
        }
        return result;
    }


    @Transactional(readOnly = true)
    public Long getViewsCount(JobListing jobListing) {
        return Optional.ofNullable(jobListing.getViewStatistics())
                .map(JobViewStatistics::getViewCount)
                .orElse(0L);
    }

    @Transactional(readOnly = true)
    public Long getApplicationsCount(JobListing jobListing) {
        return jobApplicationRepository.countApplications(jobListing);
    }

    @Transactional(readOnly = true)
    public JobListingResponseDto getJobById(Long id) {
        JobListing jobListing = findJobListingById(id);
        return jobListingMapper.toResponseDto(jobListing);
    }


}