package com.example.kegichivka.dto.resume;

import com.example.kegichivka.dto.EducationDto.EducationDto;
import com.example.kegichivka.dto.WorkExperienceDto.WorkExperienceDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResumeDto {
    private Long id;
    private Long userId;
    private String position;
    private String languages;
    private boolean remoteWork;
    private BigDecimal desiredSalary;
    private Set<String> skills = new HashSet<>();
    private List<WorkExperienceDto> workExperience = new ArrayList<>();
    private List<EducationDto> educationHistory = new ArrayList<>();
    private String aboutMe;
    private boolean visible;
}
