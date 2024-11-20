package com.example.kegichivka.maper.article;

import com.example.kegichivka.dto.article.AdminDto;
import com.example.kegichivka.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
@Named("articleAdminMapper")
public interface ArticleAdminMapper {

    AdminDto toDto(Admin admin);

    @Mapping(target = "articles", ignore = true)
    Admin toEntity(AdminDto adminDto);
}
