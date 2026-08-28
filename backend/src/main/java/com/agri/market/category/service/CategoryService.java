package com.agri.market.category.service;

import com.agri.market.category.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    List<CategoryResponseDto> getAllCategories();

    CategoryResponseDto getCategoryById(String categoryId);
}