package com.example.kegichivka.dto.WorkExperienceDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkExperienceDto {
    private Long id;
    private String company;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
