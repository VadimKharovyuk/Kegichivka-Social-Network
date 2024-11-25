package com.example.kegichivka.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private Long id;
    private String title;
    private String description;
    private String website;
    private String industry;
    private int employeesCount;
    private boolean verified;
    private LocalDateTime verificationDate;
    private LocationDto headquarters;
    private String logo;
    private Map<String, String> socialLinks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    private int totalJobListings;
    private double averageRating;
    private int reviewsCount;
}
