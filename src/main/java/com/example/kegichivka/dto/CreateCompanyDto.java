package com.example.kegichivka.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompanyDto {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @URL(message = "Website must be a valid URL")
    private String website;

    @NotBlank(message = "Industry is required")
    private String industry;

    @Min(value = 1, message = "Employees count must be at least 1")
    private int employeesCount;

    private String logo;

    @Valid
    private LocationDto headquarters;

    private Map<String, String> socialLinks;

    private boolean active = true;
}
