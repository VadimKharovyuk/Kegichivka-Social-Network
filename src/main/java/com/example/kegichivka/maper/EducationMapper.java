package com.example.kegichivka.maper;

import com.example.kegichivka.dto.EducationDto.EducationDto;
import com.example.kegichivka.model.Education;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EducationMapper {
    EducationMapper INSTANCE = Mappers.getMapper(EducationMapper.class);

    EducationDto toDto(Education education);
    Education toEntity(EducationDto educationDto);

    List<EducationDto> toDtoList(List<Education> educations);
    List<Education> toEntityList(List<EducationDto> educationDtos);
}
