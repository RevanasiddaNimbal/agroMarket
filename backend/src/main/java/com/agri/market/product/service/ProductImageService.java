package com.agri.market.product.service;

import com.agri.market.product.dto.ProductImageRequestDto;
import com.agri.market.product.dto.ProductImageResponseDto;
import com.agri.market.user.entity.User;

import java.util.List;

public interface ProductImageService {

    ProductImageResponseDto uploadImage(
            String productId,
            ProductImageRequestDto request,
            User authenticatedUser
    );

    List<ProductImageResponseDto> getProductImages(
            String productId
    );

    ProductImageResponseDto updateImage(
            String productId,
            String imageId,
            ProductImageRequestDto request,
            User authenticatedUser
    );

    void deleteImage(
            String productId,
            String imageId,
            User authenticatedUser
    );

    ProductImageResponseDto setPrimaryImage(
            String productId,
            String imageId,
            User authenticatedUser
    );
}