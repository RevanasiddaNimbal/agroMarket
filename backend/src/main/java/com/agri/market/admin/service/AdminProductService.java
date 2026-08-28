package com.agri.market.admin.service;

import com.agri.market.product.dto.ProductResponseDto;
import com.agri.market.product.dto.ProductSearchRequestDto;
import com.agri.market.product.dto.ProductSearchResponseDto;

public interface AdminProductService {

    ProductSearchResponseDto searchProducts(
            ProductSearchRequestDto request
    );

    ProductResponseDto getProductById(
            String productId
    );

    ProductResponseDto updateProductStatus(
            String productId,
            String status
    );

    void deleteProduct(
            String productId
    );
}