package com.example.kegichivka.maper;

import com.example.kegichivka.dto.WorkExperienceDto.WorkExperienceDto;
import com.example.kegichivka.model.WorkExperience;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkExperienceMapper {
    WorkExperienceMapper INSTANCE = Mappers.getMapper(WorkExperienceMapper.class);

    WorkExperienceDto toDto(WorkExperience workExperience);
    WorkExperience toEntity(WorkExperienceDto workExperienceDto);

    List<WorkExperienceDto> toDtoList(List<WorkExperience> workExperiences);
    List<WorkExperience> toEntityList(List<WorkExperienceDto> workExperienceDtos);
}
