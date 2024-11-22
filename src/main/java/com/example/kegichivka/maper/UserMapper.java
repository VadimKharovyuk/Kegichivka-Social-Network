package com.example.kegichivka.maper;

import com.example.kegichivka.dto.RegisterRequestDto;
import com.example.kegichivka.dto.UserResponseDto;
import com.example.kegichivka.model.BusinessUser;
import com.example.kegichivka.model.RegularUser;
import com.example.kegichivka.model.abstracts.BaseUser;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {NotificationMapper.class})
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", constant = "REGULAR_USER")
    @Mapping(target = "verificationStatus", constant = "UNVERIFIED")
    @Mapping(target = "accountStatus", constant = "PENDING_ACTIVATION")
    @Mapping(target = "notifications", ignore = true) // Игнорируем при создании
    RegularUser toEntity(RegisterRequestDto dto);

    @Mapping(target = "notifications", source = "notifications")
    UserResponseDto toDto(RegularUser user);

    // Перегруженный метод для BaseUser
    default UserResponseDto toDto(BaseUser user) {
        if (user instanceof RegularUser) {
            return toDto((RegularUser) user);
        }
        // Для BusinessUser или других типов возвращаем DTO без уведомлений
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .role(user.getRole())
                .accountStatus(user.getAccountStatus())
                .build();
    }

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "verificationStatus", constant = "UNVERIFIED")
    @Mapping(target = "accountStatus", constant = "PENDING_ACTIVATION")
    @Mapping(target = "notifications", ignore = true)
    RegularUser toRegularUser(RegisterRequestDto dto);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "verificationStatus", constant = "UNVERIFIED")
    @Mapping(target = "accountStatus", constant = "PENDING_ACTIVATION")
    BusinessUser toBusinessUser(RegisterRequestDto dto);

    List<UserResponseDto> toDtoList(List<? extends BaseUser> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserResponseDto dto, @MappingTarget BaseUser user);

}
