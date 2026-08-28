package com.agri.market.category.service;

import com.agri.market.category.dto.CategoryResponseDto;
import com.agri.market.category.entity.Category;
import com.agri.market.category.mapper.CategoryMapper;
import com.agri.market.category.repository.CategoryRepository;
import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        log.debug("Fetching all available categories");

        List<Category> categories = categoryRepository.findAll();

        log.debug("Fetched {} categories", categories.size());

        return categories.stream()
                .map(categoryMapper::toResponseDto)
                .toList();
    }

    @Override
    public CategoryResponseDto getCategoryById(String categoryId) {
        log.debug("Fetching category with id: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category not found with id: {}", categoryId);
                    return new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
                });

        log.debug("Category found with id: {}", categoryId);

        return categoryMapper.toResponseDto(category);
    }
}