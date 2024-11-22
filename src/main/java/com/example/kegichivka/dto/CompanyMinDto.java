package com.example.kegichivka.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompanyMinDto {
    private Long id;
    private String title;
    private String industry;
    private String logo;
    private boolean verified;
    private LocationDto headquarters;
    private int activeJobsCount;
}
