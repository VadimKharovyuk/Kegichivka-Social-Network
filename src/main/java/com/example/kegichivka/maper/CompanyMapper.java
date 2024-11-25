package com.example.kegichivka.maper;

import com.example.kegichivka.dto.CompanyDto;
import com.example.kegichivka.dto.CompanyMinDto;
import com.example.kegichivka.dto.CreateCompanyDto;
import com.example.kegichivka.dto.UpdateCompanyDto;
import com.example.kegichivka.model.Company;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LocationMapper.class})
public interface CompanyMapper {
    @Mapping(target = "totalJobListings", expression = "java(company.getJobListings().size())")
    @Mapping(target = "averageRating", expression = "java(company.getAverageRating())")
    @Mapping(target = "reviewsCount", expression = "java(company.getReviewsCount())")
    CompanyDto toDto(Company company);

    @Mapping(target = "activeJobsCount", expression = "java(company.getActiveJobListingsCount())")
    CompanyMinDto toMinDto(Company company);

    List<CompanyMinDto> toMinDtoList(List<Company> companies);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "verified", constant = "false")
    @Mapping(target = "verificationDate", ignore = true)
    @Mapping(target = "jobListings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Company toEntity(CreateCompanyDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateCompanyDto dto, @MappingTarget Company company);
}
