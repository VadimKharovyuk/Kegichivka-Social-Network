package com.example.kegichivka.dto;

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
@Getter
@Setter
public class CreateCompanyDto {
    @NotBlank(message = "Название компании обязательно")
    private String title;

    @NotBlank(message = "Описание компании обязательно")
    @Size(min = 20, max = 2000, message = "Длина описания должна быть от 20 до 2000 символов")
    private String description;

    @URL(message = "Некорректный URL сайта")
    private String website;

    @NotBlank(message = "Укажите отрасль")
    private String industry;

    @Min(value = 1, message = "Количество сотрудников должно быть больше 0")
    private int employeesCount;

    @NotNull(message = "Укажите местоположение штаб-квартиры")
    private LocationDto headquarters;

    private String logo;

    private Map<String, @URL(message = "Некорректный URL социальной сети") String> socialLinks;
}
