package com.example.kegichivka.maper;

import com.example.kegichivka.dto.LocationDto;
import com.example.kegichivka.model.Location;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location toEntity(LocationDto dto);
    LocationDto toDto(Location location);
}
