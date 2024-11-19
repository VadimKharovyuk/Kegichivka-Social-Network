package com.example.kegichivka.maper;

import com.example.kegichivka.dto.RegisterRequestDto;
import com.example.kegichivka.dto.UserResponseDto;
import com.example.kegichivka.model.BusinessUser;
import com.example.kegichivka.model.RegularUser;
import com.example.kegichivka.model.abstracts.BaseUser;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", constant = "REGULAR_USER")
    @Mapping(target = "verificationStatus", constant = "UNVERIFIED")
    @Mapping(target = "accountStatus", constant = "PENDING_ACTIVATION")
    RegularUser toEntity(RegisterRequestDto dto);

    UserResponseDto toDto(BaseUser user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "verificationStatus", constant = "UNVERIFIED")
    @Mapping(target = "accountStatus", constant = "PENDING_ACTIVATION")
    RegularUser toRegularUser(RegisterRequestDto dto);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "verificationStatus", constant = "UNVERIFIED")
    @Mapping(target = "accountStatus", constant = "PENDING_ACTIVATION")
    BusinessUser toBusinessUser(RegisterRequestDto dto);





    // Для списков
    List<UserResponseDto> toDtoList(List<? extends BaseUser> users);

    // Для частичного обновления
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserResponseDto dto, @MappingTarget BaseUser user);
}
