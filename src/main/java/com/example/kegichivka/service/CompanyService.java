package com.example.kegichivka.service;

import com.example.kegichivka.dto.CompanyDto;
import com.example.kegichivka.dto.CompanySearchDto;
import com.example.kegichivka.dto.CreateCompanyDto;
import com.example.kegichivka.dto.UpdateCompanyDto;
import com.example.kegichivka.exception.ResourceNotFoundException;
import com.example.kegichivka.maper.CompanyMapper;
import com.example.kegichivka.model.Company;
import com.example.kegichivka.repositoty.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final UserService userService;

    public CompanyDto createCompany(CreateCompanyDto dto) {
        log.debug("Creating new company with title: {}", dto.getTitle());

        Company company = companyMapper.toEntity(dto);
        company.setCreatedAt(LocalDateTime.now());
        company.setUpdatedAt(LocalDateTime.now());

        Company savedCompany = companyRepository.save(company);
        return companyMapper.toDto(savedCompany);
    }

    public CompanyDto updateCompany(Long id, UpdateCompanyDto dto) {
        log.debug("Updating company with id: {}", id);

        Company company = findCompanyById(id);
        companyMapper.updateEntityFromDto(dto, company);
        company.setUpdatedAt(LocalDateTime.now());

        Company updatedCompany = companyRepository.save(company);
        return companyMapper.toDto(updatedCompany);
    }

    @Transactional(readOnly = true)
    public CompanyDto getCompanyById(Long id) {
        return companyMapper.toDto(findCompanyById(id));
    }

    @Transactional(readOnly = true)
    public Page<CompanyDto> getAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable)
                .map(companyMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<CompanyDto> searchCompanies(CompanySearchDto searchDto, Pageable pageable) {
        Specification<Company> spec = createSearchSpecification(searchDto);
        return companyRepository.findAll(spec, pageable)
                .map(companyMapper::toDto);
    }



    public CompanyDto verifyCompany(Long id) {
        Company company = findCompanyById(id);
        company.setVerified(true);
        company.setVerificationDate(LocalDateTime.now());
        company.setUpdatedAt(LocalDateTime.now());

        Company verifiedCompany = companyRepository.save(company);
        return companyMapper.toDto(verifiedCompany);
    }

    public void deleteCompany(Long id) {
        Company company = findCompanyById(id);
        companyRepository.delete(company);
    }

    private Company findCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
    }

    private Specification<Company> createSearchSpecification(CompanySearchDto searchDto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(searchDto.getKeyword())) {
                String pattern = "%" + searchDto.getKeyword().toLowerCase() + "%";
                predicates.add((Predicate) cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("industry")), pattern)
                ));
            }

            if (searchDto.getVerified() != null) {
                predicates.add(cb.equal(root.get("verified"), searchDto.getVerified()));
            }

            if (searchDto.getMinEmployees() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("employeesCount"),
                        searchDto.getMinEmployees()));
            }

            if (searchDto.getMaxEmployees() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("employeesCount"),
                        searchDto.getMaxEmployees()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
