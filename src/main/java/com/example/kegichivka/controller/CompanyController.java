package com.example.kegichivka.controller;

import com.example.kegichivka.dto.CompanyDto;
import com.example.kegichivka.dto.CompanySearchDto;
import com.example.kegichivka.dto.CreateCompanyDto;
import com.example.kegichivka.exception.ResourceNotFoundException;
import com.example.kegichivka.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/companies")
@RequiredArgsConstructor
@Slf4j
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping
    public String listCompanies(
            Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(required = false) Integer minEmployees,
            @RequestParam(required = false) Integer maxEmployees,
            @RequestParam(required = false) String industry,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        try {
            CompanySearchDto searchDto = CompanySearchDto.builder()
                    .keyword(keyword)
                    .verified(verified)
                    .minEmployees(minEmployees)
                    .maxEmployees(maxEmployees)
                    .industry(industry)
                    .build();

            Page<CompanyDto> companies = companyService.searchCompanies(searchDto, pageable);

            model.addAttribute("companies", companies);
            model.addAttribute("keyword", keyword);
            model.addAttribute("currentSearch", searchDto);
            model.addAttribute("hasSearch", hasSearchCriteria(searchDto));

        } catch (Exception e) {
            log.error("Error during company search", e);
            model.addAttribute("error", "Произошла ошибка при поиске. Попробуйте позже.");
        }

        return "companies/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("companyDto", new CreateCompanyDto());
        return "companies/create";
    }

    @PostMapping("/create")
    public String createCompany(
            @Valid @ModelAttribute("companyDto") CreateCompanyDto companyDto,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            return "companies/create";
        }

        try {
            CompanyDto createdCompany = companyService.createCompany(companyDto);
            return "redirect:/companies/" + createdCompany.getId();
        } catch (Exception e) {
            log.error("Error creating company", e);
            model.addAttribute("error", "Failed to create company");
            return "companies/create";
        }
    }

    @GetMapping("/{id}")
    public String showCompany(@PathVariable Long id, Model model) {
        try {
            CompanyDto company = companyService.getCompanyById(id);
            model.addAttribute("company", company);
            return "companies/detail";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "error/404";
        }
    }

    private boolean hasSearchCriteria(CompanySearchDto searchDto) {
        return StringUtils.hasText(searchDto.getKeyword()) ||
                searchDto.getVerified() != null ||
                searchDto.getMinEmployees() != null ||
                searchDto.getMaxEmployees() != null ||
                StringUtils.hasText(searchDto.getIndustry());
    }
}
