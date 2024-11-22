package com.example.kegichivka.dto;

import com.example.kegichivka.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationDto {
    @NotNull
    private NotificationType type;

    @NotNull
    @Size(min = 1, max = 500)
    private String message;

    private Long relatedJobId;
}
