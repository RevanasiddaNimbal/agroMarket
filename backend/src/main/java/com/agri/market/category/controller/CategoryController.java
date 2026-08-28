package com.agri.market.category.controller;

import com.agri.market.category.dto.CategoryResponseDto;
import com.agri.market.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Categories",
        description = "APIs for retrieving product categories"
)
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Get all categories",
            description = "Returns all available product categories."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categories retrieved successfully"
            )
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {

        log.info("Request received to fetch all categories");

        final List<CategoryResponseDto> response =
                categoryService.getAllCategories();

        log.info(
                "Successfully fetched {} categories",
                response.size()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get category by ID",
            description = "Returns a product category using its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            )
    })
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(
            @PathVariable final String categoryId
    ) {

        log.info(
                "Request received to fetch category: {}",
                categoryId
        );

        final CategoryResponseDto response =
                categoryService.getCategoryById(categoryId);

        log.info(
                "Successfully fetched category: {}",
                categoryId
        );

        return ResponseEntity.ok(response);
    }
}