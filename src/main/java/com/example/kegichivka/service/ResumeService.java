package com.example.kegichivka.service;

import com.example.kegichivka.dto.resume.ResumeDto;
import com.example.kegichivka.maper.ResumeMapper.ResumeMapper;
import com.example.kegichivka.model.RegularUser;
import com.example.kegichivka.model.Resume;
import com.example.kegichivka.repositoty.RegularUserRepository;
import com.example.kegichivka.repositoty.ResumeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeMapper resumeMapper;
    private final ResumeRepository resumeRepository;
    private final RegularUserRepository regularUserRepository;

    public ResumeDto createResume(ResumeDto resumeDto, Long userId) {
        RegularUser user = regularUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Resume resume = resumeMapper.toEntity(resumeDto);
        resume.setUser(user);
        resume = resumeRepository.save(resume);
        return resumeMapper.toDto(resume);
    }

    public ResumeDto findResume(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found with id: " + id));
        return resumeMapper.toDto(resume);
    }
    public List<ResumeDto> getCurrentUserResumes(Long userId) {
        List<Resume> resumes = resumeRepository.findByUserId(userId);
        return resumeMapper.toDtoList(resumes);
    }

    public void deleteResume(Long id) {
        resumeRepository.deleteById(id);
    }

    public void toggleVisibility(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found"));
        resume.setVisible(!resume.isVisible());
        resumeRepository.save(resume);
    }
}
