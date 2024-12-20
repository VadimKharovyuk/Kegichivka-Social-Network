package com.example.kegichivka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyDto {
    private String title;
    private String description;
    private String website;
    private String industry;
    private Integer employeesCount;
    private String logo;
    private LocationDto headquarters;
    private Map<String, String> socialLinks;
    private Boolean active;
}
