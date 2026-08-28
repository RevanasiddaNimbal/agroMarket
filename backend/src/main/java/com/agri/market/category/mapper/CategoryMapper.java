package com.agri.market.category.mapper;

import com.agri.market.category.dto.CategoryResponseDto;
import com.agri.market.category.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CategoryMapper {

    public CategoryResponseDto toResponseDto(Category category) {
        log.debug("Mapping category to response DTO: {}", category.getId());

        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}