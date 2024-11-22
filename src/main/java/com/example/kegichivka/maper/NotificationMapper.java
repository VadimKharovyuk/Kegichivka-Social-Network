package com.example.kegichivka.maper;

import com.example.kegichivka.dto.NotificationDto;
import com.example.kegichivka.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "relatedJobId", source = "relatedJob.id")
    NotificationDto toDto(Notification notification);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "relatedJob", ignore = true)
    Notification toEntity(NotificationDto dto);

    List<NotificationDto> toDtoList(List<Notification> notifications);
}
