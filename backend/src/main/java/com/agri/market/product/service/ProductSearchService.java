package com.agri.market.product.service;

import com.agri.market.product.dto.ProductSearchRequestDto;
import com.agri.market.product.dto.ProductSearchResponseDto;

public interface ProductSearchService {

    ProductSearchResponseDto searchProducts(
            ProductSearchRequestDto request
    );

    ProductSearchResponseDto searchMyProducts(
            ProductSearchRequestDto request,
            String userId
    );
}