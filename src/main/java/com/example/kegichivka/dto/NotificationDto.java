package com.example.kegichivka.dto;

import com.example.kegichivka.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String message;
    private NotificationType type;
    private LocalDateTime createdAt;
    private boolean isRead;
    private Long relatedJobId; // ID связанной вакансии
}
