package com.example.kegichivka.maper.admin;

import com.example.kegichivka.dto.admin.AdminResponse;
import com.example.kegichivka.dto.admin.CreateAdminRequest;
import com.example.kegichivka.dto.admin.UpdateAdminRequest;
import com.example.kegichivka.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    @Named("adminMapper")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "news", ignore = true)
    @Mapping(target = "role", constant = "ADMIN")
    @Mapping(target = "verificationStatus", constant = "UNVERIFIED")
    @Mapping(target = "accountStatus", constant = "PENDING_ACTIVATION")
        // Уберите следующую строку, если она есть
        // @Mapping(target = "verificationToken", ignore = true)
    Admin toEntity(CreateAdminRequest request);

    AdminResponse toResponse(Admin admin);

    void updateAdminFromDto(UpdateAdminRequest request, @MappingTarget Admin admin);
}
