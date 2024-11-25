package com.example.kegichivka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySearchDto {
    private String keyword;
    private Boolean verified;
    private Integer minEmployees;
    private Integer maxEmployees;
    private String industry;
    private String location;
}
