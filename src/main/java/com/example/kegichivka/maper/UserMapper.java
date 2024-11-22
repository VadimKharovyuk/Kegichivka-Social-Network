package com.example.kegichivka.maper;

import com.example.kegichivka.dto.RegisterRequestDto;
import com.example.kegichivka.dto.UserResponseDto;
import com.example.kegichivka.dto.article.BaseUserDto;
import com.example.kegichivka.model.BusinessUser;
import com.example.kegichivka.model.RegularUser;
import com.example.kegichivka.model.abstracts.BaseUser;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {NotificationMapper.class})
public interface UserMapper {

    default BaseUserDto toBaseUserDto(BaseUser user) {
        if (user == null) {
            return null;
        }
        return BaseUserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .role(user.getRole())
                .build();
    }

    // Специальный метод для BusinessUser
    default BaseUserDto toBaseUserDto(BusinessUser user) {
        return toBaseUserDto((BaseUser) user);
    }

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", constant = "REGULAR_USER")
    @Mapping(target = "verificationStatus", constant = "UNVERIFIED")
    @Mapping(target = "accountStatus", constant = "PENDING_ACTIVATION")
    @Mapping(target = "notifications", ignore = true)
    RegularUser toEntity(RegisterRequestDto dto);

    @Mapping(target = "notifications", source = "notifications")
    UserResponseDto toDto(RegularUser user);

    default UserResponseDto toDto(BaseUser user) {
        if (user instanceof RegularUser) {
            return toDto((RegularUser) user);
        }
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

}
