package com.example.kegichivka.controller;

import com.example.kegichivka.dto.*;
import com.example.kegichivka.enums.UserRole;
import com.example.kegichivka.exception.ResourceNotFoundException;
import com.example.kegichivka.model.BusinessUser;
import com.example.kegichivka.model.Category;
import com.example.kegichivka.model.JobListing;
import com.example.kegichivka.model.RegularUser;
import com.example.kegichivka.model.abstracts.BaseUser;
import com.example.kegichivka.repositoty.BusinessUserRepository;
import com.example.kegichivka.repositoty.CategoryRepository;
import com.example.kegichivka.service.BusinessUserService;
import com.example.kegichivka.service.JobListingService;
import com.example.kegichivka.service.UserPrincipal;
import com.example.kegichivka.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobListingWebController {
    private final JobListingService jobListingService;
    private final CategoryRepository categoryRepository;
    private final BusinessUserService businessUserService;
    private final UserService userService;

    @GetMapping
    public String listJobs(
            Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            @RequestParam(required = false) Boolean isRemote,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        JobSearchDto searchDto = JobSearchDto.builder()
                .keyword(keyword)
                .categoryIds(categoryIds)
                .minSalary(minSalary)
                .maxSalary(maxSalary)
                .isRemote(isRemote)
                .build();

        Page<JobListingShortDto> jobs = jobListingService.searchJobs(searchDto, pageable);

        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("currentSearch", searchDto);

        return "jobs/list";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('BUSINESS_USER')")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("jobDto")) {
            model.addAttribute("jobDto", new CreateJobListingDto());
        }
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "jobs/create";
    }

    @PostMapping("/create")
    public String createJob(
            @Valid @ModelAttribute("jobDto") CreateJobListingDto jobDto,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        log.info("Attempting to create job listing: {}", jobDto.getTitle());

        if (result.hasErrors()) {
            log.warn("Validation errors: {}", result.getAllErrors());
            model.addAttribute("categories", categoryRepository.findAll());
            return "jobs/create";
        }

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            log.debug("User principal email: {}", userPrincipal.getEmail());

            BusinessUser businessUser = businessUserService.getBusinessUserByEmail(userPrincipal.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("BusinessUser not found"));
            log.debug("Found business user: {}", businessUser.getId());

            JobListingResponseDto created = jobListingService.createJob(jobDto, businessUser);
            log.info("Successfully created job listing with ID: {}", created.getId());

            redirectAttributes.addFlashAttribute("successMessage", "Вакансия успешно создана");
            return "redirect:/jobs/" + created.getId();
        } catch (Exception e) {
            log.error("Error creating job listing", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании вакансии: " + e.getMessage());
            redirectAttributes.addFlashAttribute("jobDto", jobDto);
            return "redirect:/jobs/create";
        }
    }

    @GetMapping("/{id}")
    public String viewJob(@PathVariable Long id, Model model, @AuthenticationPrincipal UserPrincipal currentUser) {
        JobListingResponseDto job = jobListingService.getJobById(id);
        model.addAttribute("job", job);
        model.addAttribute("currentUserId", currentUser.getId());
        model.addAttribute("currentUserRole", currentUser.getRole());

        if (currentUser.getRole() == UserRole.REGULAR_USER) {
            JobListing jobListing = jobListingService.findJobListingById(id);
            RegularUser regularUser = userService.getRegularUserById(currentUser.getId());
            boolean canApply = jobListingService.canApply(regularUser, jobListing);
            model.addAttribute("canApply", canApply);
        }

        return "jobs/view";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('BUSINESS_USER')")
    public String showEditForm(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal BusinessUser businessUser,
            RedirectAttributes redirectAttributes
    ) {
        JobListingResponseDto job = jobListingService.getJobById(id);
        model.addAttribute("jobDto", job);
        model.addAttribute("categories", categoryRepository.findAll());
        return "jobs/edit";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('BUSINESS_USER')")
    public String updateJob(
            @PathVariable Long id,
            @Valid @ModelAttribute("jobDto") UpdateJobListingDto jobDto,
            BindingResult result,
            @AuthenticationPrincipal BusinessUser businessUser,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "jobs/edit";
        }

        try {
            jobListingService.updateJob(id, jobDto, businessUser);
            redirectAttributes.addFlashAttribute("successMessage", "Вакансия успешно обновлена");
            return "redirect:/jobs/" + id;
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "У вас нет прав на редактирование этой вакансии");
            return "redirect:/jobs/" + id;
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при обновлении вакансии: " + e.getMessage());
            model.addAttribute("categories", categoryRepository.findAll());
            return "jobs/edit";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('BUSINESS_USER')")
    public String deleteJob(
            @PathVariable Long id,
            @AuthenticationPrincipal BusinessUser businessUser,
            RedirectAttributes redirectAttributes
    ) {
        try {
            jobListingService.deleteJob(id, businessUser);
            redirectAttributes.addFlashAttribute("successMessage", "Вакансия успешно удалена");
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "У вас нет прав на удаление этой вакансии");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении вакансии: " + e.getMessage());
        }
        return "redirect:/jobs";
    }

    // Обработка ошибок
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/jobs";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "У вас нет прав для выполнения этого действия");
        return "redirect:/jobs";
    }

}
