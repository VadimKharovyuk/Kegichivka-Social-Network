package com.example.kegichivka.controller;

import com.example.kegichivka.config.SecurityUtils;
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
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.util.StringUtils;
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
    private final SecurityUtils securityUtils;

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
        try {
            JobSearchDto searchDto = JobSearchDto.builder()
                    .keyword(keyword)
                    .categoryIds(categoryIds)
                    .minSalary(minSalary)
                    .maxSalary(maxSalary)
                    .isRemote(isRemote)
                    .build();

            // Получаем результаты поиска
            Page<JobListingShortDto> searchResults = jobListingService.searchJobs(searchDto, pageable);

            // Получаем активные вакансии только если нет результатов поиска
            Page<JobListingShortDto> activeJobs = null;
            if (hasSearchCriteria(searchDto) && searchResults.isEmpty()) {
                // Если был поиск и он пустой - показываем сообщение
                model.addAttribute("noSearchResults", true);
            } else if (!hasSearchCriteria(searchDto)) {
                // Если поиска не было - показываем активные вакансии
                activeJobs = jobListingService.getActiveJobs(pageable);
            }

            model.addAttribute("searchResults", searchResults);
            model.addAttribute("activeJobs", activeJobs);
            model.addAttribute("keyword", keyword);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("currentSearch", searchDto);
            model.addAttribute("hasSearch", hasSearchCriteria(searchDto));

        } catch (Exception e) {
            log.error("Error during job search", e);
            model.addAttribute("error", "Произошла ошибка при поиске. Попробуйте позже.");
        }

        return "jobs/list";
    }

    private boolean hasSearchCriteria(JobSearchDto searchDto) {
        return StringUtils.hasText(searchDto.getKeyword()) ||
                searchDto.getMinSalary() != null ||
                searchDto.getMaxSalary() != null ||
                (searchDto.getCategoryIds() != null && !searchDto.getCategoryIds().isEmpty()) ||
                searchDto.getIsRemote() != null;
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('BUSINESS_USER')")
    public String showCreateForm(Model model) {
        model.containsAttribute("jobDto");
        model.addAttribute("jobDto", new CreateJobListingDto());
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
    public String viewJob(@PathVariable Long id,
                          Model model,
                          @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            // Получаем информацию о вакансии
            JobListingResponseDto job = jobListingService.getJobById(id);
            model.addAttribute("job", job);

            // Проверяем аутентификацию пользователя
            if (currentUser != null) {
                model.addAttribute("currentUserId", currentUser.getId());
                model.addAttribute("currentUserRole", currentUser.getRole());

                // Проверяем возможность подачи заявки только для обычных пользователей
                if (currentUser.getRole() == UserRole.REGULAR_USER) {
                    JobListing jobListing = jobListingService.findJobListingById(id);
                    RegularUser regularUser = userService.getRegularUserById(currentUser.getId());
                    boolean canApply = jobListingService.canApply(regularUser, jobListing);
                    model.addAttribute("canApply", canApply);
                }
            } else {
                // Для неаутентифицированных пользователей
                model.addAttribute("currentUserRole", null);
                model.addAttribute("canApply", false);
            }

            return "jobs/view";

        } catch (EntityNotFoundException e) {
            // Обработка случая, когда вакансия не найдена
            model.addAttribute("errorMessage", "Job listing not found");
            return "redirect:/jobs";
        }
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


    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "У вас нет прав для выполнения этого действия");
        return "redirect:/jobs";
    }

    //    @PostMapping("/{id}/apply")
//    @PreAuthorize("hasRole('REGULAR_USER')")
//    public String applyForJob(@PathVariable Long id,
//                              @AuthenticationPrincipal UserPrincipal currentUser,
//                              RedirectAttributes redirectAttributes) {
//        try {
//            JobListing jobListing = jobListingService.findJobListingById(id);
//            RegularUser regularUser = userService.getRegularUserById(currentUser.getId());
//
//            if (jobListingService.canApply(regularUser, jobListing)) {
//                jobListingService.applyForJob(regularUser, jobListing);
//                redirectAttributes.addFlashAttribute("successMessage",
//                        "Your application has been submitted successfully!");
//            } else {
//                redirectAttributes.addFlashAttribute("errorMessage",
//                        "You have already applied for this position");
//            }
//
//            return "redirect:/jobs/" + id;
//
//        } catch (EntityNotFoundException e) {
//            redirectAttributes.addFlashAttribute("errorMessage",
//                    "Unable to process your application. Please try again.");
//            return "redirect:/jobs/" + id;
//        }
//    }
    @GetMapping("/my-jobs")
    public String getMyJobs(Model model) throws AccessDeniedException {
        String email = securityUtils.getCurrentUserEmail();
        List<JobListingResponseDto> jobs = jobListingService.getCurrentUserJobs(email);
        model.addAttribute("jobs", jobs);
        return "jobs/my-jobs";
    }

    @GetMapping("/my-jobs/{id}")
    public String getJob(@PathVariable Long id, Model model) {
        JobListingResponseDto job = jobListingService.getJobById(id);
        model.addAttribute("job", job);
        return "jobs/job-details";
    }

    // Обработка ошибок
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/jobs";
    }

}
