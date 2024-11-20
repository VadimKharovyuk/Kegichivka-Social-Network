package com.example.kegichivka.maper.admin;

import com.example.kegichivka.dto.admin.AdminDashboardDto;
import com.example.kegichivka.dto.admin.AdminResponse;
import com.example.kegichivka.dto.admin.CreateAdminRequest;
import com.example.kegichivka.dto.admin.UpdateAdminRequest;
import com.example.kegichivka.model.Admin;
import org.mapstruct.*;

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




    @Mapping(target = "admin", source = "admin")
    @Mapping(target = "totalUsers", constant = "0L")  // Заглушка, позже заменим на реальные данные
    @Mapping(target = "totalNews", constant = "0L")   // Заглушка, позже заменим на реальные данные
    @Mapping(target = "totalArticles", constant = "0L") // Заглушка, позже заменим на реальные данные
    AdminDashboardDto toDto(Admin admin);

    @InheritInverseConfiguration
    Admin fromDto(AdminDashboardDto dto);

}
