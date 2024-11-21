package com.example.kegichivka.maper.ResumeMapper;

import com.example.kegichivka.dto.resume.ResumeDto;
import com.example.kegichivka.model.Resume;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResumeMapper {
    ResumeMapper INSTANCE = Mappers.getMapper(ResumeMapper.class);

    @Mapping(source = "user.id", target = "userId")
    ResumeDto toDto(Resume resume);

    @Mapping(source = "userId", target = "user.id")
    Resume toEntity(ResumeDto resumeDto);

    List<ResumeDto> toDtoList(List<Resume> resumes);
    List<Resume> toEntityList(List<ResumeDto> resumeDtos);
}
