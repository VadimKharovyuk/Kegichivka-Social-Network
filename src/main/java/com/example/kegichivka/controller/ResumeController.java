package com.example.kegichivka.controller;

import com.example.kegichivka.dto.resume.ResumeDto;
import com.example.kegichivka.model.RegularUser;
import com.example.kegichivka.service.ResumeService;
import com.example.kegichivka.service.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Controller
@RequestMapping("/resumes")
@RequiredArgsConstructor

public class ResumeController {
    private final ResumeService resumeService;

    @GetMapping("/create")
    public String showCreateForm(Model model, @AuthenticationPrincipal UserPrincipal currentUser) {
        model.addAttribute("resumeDto", new ResumeDto());
        return "resumes/create";
    }

    @PostMapping("/create")
    public String createResume(@ModelAttribute ResumeDto resumeDto,
                               @AuthenticationPrincipal UserPrincipal currentUser) {
        ResumeDto savedResume = resumeService.createResume(resumeDto, currentUser.getId());
        return "redirect:/resumes/" + savedResume.getId();
    }

    @GetMapping("/{id}")
    public String viewResume(@PathVariable Long id, Model model) {
        try {
            ResumeDto resume = resumeService.findResume(id);
            model.addAttribute("resume", resume);
            return "resumes/view";
        } catch (EntityNotFoundException e) {
            return "redirect:/error";
        }
    }

    @GetMapping("/my-resumes")
    public String myResumes(@AuthenticationPrincipal UserPrincipal currentUser, Model model) {
        List<ResumeDto> resumes = resumeService.getCurrentUserResumes(currentUser.getId());
        model.addAttribute("resumes", resumes);
        return "resumes/my-resumes";
    }
    @PostMapping("/{id}")
    public String deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return "redirect:/resumes/my-resumes";
    }

    @PostMapping("/{id}/visibility")
    public String toggleVisibility(@PathVariable Long id) {
        resumeService.toggleVisibility(id);
        return "redirect:/resumes/my-resumes";
    }

    @GetMapping("/{id}/edit")
    public String editResume(@PathVariable Long id, Model model) {
        ResumeDto resume = resumeService.findResume(id);
        model.addAttribute("resumeDto", resume);
        return "resumes/edit";
    }
}
