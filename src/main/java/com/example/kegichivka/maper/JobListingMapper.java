package com.example.kegichivka.maper;

import com.example.kegichivka.dto.CreateJobListingDto;
import com.example.kegichivka.dto.JobListingResponseDto;
import com.example.kegichivka.dto.JobListingShortDto;
import com.example.kegichivka.dto.UpdateJobListingDto;
import com.example.kegichivka.model.BusinessUser;
import com.example.kegichivka.model.Category;
import com.example.kegichivka.model.JobListing;
import com.example.kegichivka.repositoty.JobApplicationRepository;
import com.example.kegichivka.repositoty.ViewStatisticsRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;


import com.example.kegichivka.dto.CreateJobListingDto;
import com.example.kegichivka.dto.JobListingResponseDto;
import com.example.kegichivka.dto.JobListingShortDto;
import com.example.kegichivka.dto.UpdateJobListingDto;
import com.example.kegichivka.model.BusinessUser;
import com.example.kegichivka.model.Category;
import com.example.kegichivka.model.JobListing;
import com.example.kegichivka.model.JobViewStatistics;
import com.example.kegichivka.repositoty.JobApplicationRepository;
import com.example.kegichivka.repositoty.ViewStatisticsRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
@Mapper(componentModel = "spring", uses = {CategoryMapper.class, CompanyMapper.class})
public abstract class JobListingMapper {
    @Autowired
    protected JobApplicationRepository jobApplicationRepository;



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", constant = "0L")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "category", source = "dto.categoryId")
    @Mapping(target = "businessUser", source = "businessUser")
    @Mapping(target = "viewStatistics", ignore = true)
    @Mapping(target = "jobApplications", ignore = true)
    @Mapping(target = "applications", ignore = true)
    @Mapping(target = "company", ignore = true)
    public abstract JobListing toEntity(CreateJobListingDto dto, BusinessUser businessUser);

    @Mapping(target = "applicationsCount", expression = "java(getApplicationsCount(jobListing))")
    @Mapping(target = "viewsCount", expression = "java(getViewsCount(jobListing))")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "businessUser", source = "businessUser")
    public abstract JobListingResponseDto toResponseDto(JobListing jobListing);


    public abstract JobListingShortDto toShortDto(JobListing jobListing);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntityFromDto(UpdateJobListingDto dto, @MappingTarget JobListing entity);

    protected Long getApplicationsCount(JobListing jobListing) {
        return jobApplicationRepository.countApplications(jobListing);
    }

    protected Long getViewsCount(JobListing jobListing) {
        return Optional.ofNullable(jobListing.getViewStatistics())
                .map(JobViewStatistics::getViewCount)
                .orElse(0L);
    }
    // Реализация базового маппинга из JobListingResponseDto
    @AfterMapping
    protected void afterMapping(CreateJobListingDto dto, @MappingTarget JobListing jobListing) {
        jobListing.setRemote(dto.isRemote());
    }


    protected Category mapCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }
}

