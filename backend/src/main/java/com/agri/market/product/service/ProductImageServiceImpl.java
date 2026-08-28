package com.agri.market.product.service;

import com.agri.market.cloudinary.service.CloudinaryService;
import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.product.dto.ProductImageRequestDto;
import com.agri.market.product.dto.ProductImageResponseDto;
import com.agri.market.product.entity.Product;
import com.agri.market.product.entity.ProductImage;
import com.agri.market.product.mapper.ProductImageMapper;
import com.agri.market.product.repository.ProductImageRepository;
import com.agri.market.product.repository.ProductRepository;
import com.agri.market.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageServiceImpl
        implements ProductImageService {

    private static final long MAX_FILE_SIZE =
            5 * 1024 * 1024;

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductImageMapper productImageMapper;

    @Override
    @Transactional
    public ProductImageResponseDto uploadImage(
            final String productId,
            final ProductImageRequestDto request,
            final User authenticatedUser
    ) {

        log.info(
                "Uploading product image. Product: {}, User: {}",
                productId,
                authenticatedUser.getId()
        );

        final Product product =
                getProduct(productId);

        validateProductAccess(
                product,
                authenticatedUser
        );

        validateImage(
                request.getImage()
        );

        final String imageUrl =
                cloudinaryService.uploadProductImage(
                        request.getImage()
                );

        final ProductImage productImage =
                ProductImage.builder()
                        .product(product)
                        .imageUrl(imageUrl)
                        .primary(request.isPrimary())
                        .displayOrder(request.getDisplayOrder())
                        .build();

        if (request.isPrimary()) {
            removeExistingPrimaryImage(productId);
        }

        final ProductImage savedImage =
                productImageRepository.save(
                        productImage
                );

        log.info(
                "Product image uploaded successfully. Image: {}, Product: {}",
                savedImage.getId(),
                productId
        );

        return productImageMapper.toResponseDto(
                savedImage
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageResponseDto> getProductImages(
            final String productId
    ) {

        log.debug(
                "Fetching product images. Product: {}",
                productId
        );

        getProduct(productId);

        return productImageRepository
                .findAllByProduct_IdOrderByDisplayOrderAsc(
                        productId
                )
                .stream()
                .map(productImageMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductImageResponseDto updateImage(
            final String productId,
            final String imageId,
            final ProductImageRequestDto request,
            final User authenticatedUser
    ) {

        log.info(
                "Updating product image. Product: {}, Image: {}, User: {}",
                productId,
                imageId,
                authenticatedUser.getId()
        );

        final Product product =
                getProduct(productId);

        validateProductAccess(
                product,
                authenticatedUser
        );

        final ProductImage productImage =
                productImageRepository
                        .findByIdAndProduct_Id(
                                imageId,
                                productId
                        )
                        .orElseThrow(() -> {
                            log.warn(
                                    "Product image not found. Image: {}, Product: {}",
                                    imageId,
                                    productId
                            );

                            return new BusinessException(
                                    ErrorCode.PRODUCT_IMAGE_NOT_FOUND
                            );
                        });

        if (request.getImage() != null
                && !request.getImage().isEmpty()) {

            validateImage(
                    request.getImage()
            );

            final String imageUrl =
                    cloudinaryService.uploadProductImage(
                            request.getImage()
                    );

            productImage.setImageUrl(
                    imageUrl
            );
        }

        if (request.isPrimary()) {
            removeExistingPrimaryImage(
                    productId
            );
        }

        productImage.setPrimary(
                request.isPrimary()
        );

        productImage.setDisplayOrder(
                request.getDisplayOrder()
        );

        final ProductImage updatedImage =
                productImageRepository.save(
                        productImage
                );

        log.info(
                "Product image updated successfully. Image: {}",
                imageId
        );

        return productImageMapper.toResponseDto(
                updatedImage
        );
    }

    @Override
    @Transactional
    public void deleteImage(
            final String productId,
            final String imageId,
            final User authenticatedUser
    ) {

        log.info(
                "Deleting product image. Product: {}, Image: {}, User: {}",
                productId,
                imageId,
                authenticatedUser.getId()
        );

        final Product product =
                getProduct(productId);

        validateProductAccess(
                product,
                authenticatedUser
        );

        final ProductImage productImage =
                productImageRepository
                        .findByIdAndProduct_Id(
                                imageId,
                                productId
                        )
                        .orElseThrow(() -> {
                            log.warn(
                                    "Product image not found. Image: {}, Product: {}",
                                    imageId,
                                    productId
                            );

                            return new BusinessException(
                                    ErrorCode.PRODUCT_IMAGE_NOT_FOUND
                            );
                        });

        productImageRepository.delete(
                productImage
        );

        log.info(
                "Product image deleted successfully. Image: {}",
                imageId
        );
    }

    @Override
    @Transactional
    public ProductImageResponseDto setPrimaryImage(
            final String productId,
            final String imageId,
            final User authenticatedUser
    ) {

        log.info(
                "Setting primary product image. Product: {}, Image: {}, User: {}",
                productId,
                imageId,
                authenticatedUser.getId()
        );

        final Product product =
                getProduct(productId);

        validateProductAccess(
                product,
                authenticatedUser
        );

        final ProductImage productImage =
                productImageRepository
                        .findByIdAndProduct_Id(
                                imageId,
                                productId
                        )
                        .orElseThrow(() -> {
                            log.warn(
                                    "Product image not found. Image: {}, Product: {}",
                                    imageId,
                                    productId
                            );

                            return new BusinessException(
                                    ErrorCode.PRODUCT_IMAGE_NOT_FOUND
                            );
                        });

        removeExistingPrimaryImage(
                productId
        );

        productImage.setPrimary(
                true
        );

        final ProductImage savedImage =
                productImageRepository.save(
                        productImage
                );

        log.info(
                "Primary product image updated successfully. Image: {}",
                imageId
        );

        return productImageMapper.toResponseDto(
                savedImage
        );
    }

    private Product getProduct(
            final String productId
    ) {

        return productRepository
                .findById(productId)
                .orElseThrow(() -> {
                    log.warn(
                            "Product not found: {}",
                            productId
                    );

                    return new BusinessException(
                            ErrorCode.PRODUCT_NOT_FOUND
                    );
                });
    }

    private void validateProductAccess(
            final Product product,
            final User authenticatedUser
    ) {

        if (authenticatedUser == null) {

            log.warn(
                    "Product image access rejected because authenticated user is null"
            );

            throw new BusinessException(
                    ErrorCode.USER_NOT_FOUND
            );
        }

        if (isAdmin(authenticatedUser)) {

            log.debug(
                    "Admin product image access granted. User: {}, Product: {}",
                    authenticatedUser.getId(),
                    product.getId()
            );

            return;
        }

        if (product.getFarmer() == null
                || !product.getFarmer()
                .getId()
                .equals(authenticatedUser.getId())) {

            log.warn(
                    "Product image access denied. User: {}, Product: {}",
                    authenticatedUser.getId(),
                    product.getId()
            );

            throw new BusinessException(
                    ErrorCode.PRODUCT_NOT_FOUND
            );
        }
    }

    private boolean isAdmin(
            final User user
    ) {

        return user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(
                        "ROLE_ADMIN"::equals
                );
    }

    private void validateImage(
            final MultipartFile image
    ) {

        if (image == null || image.isEmpty()) {

            log.warn(
                    "Product image validation failed: empty file"
            );

            throw new BusinessException(
                    ErrorCode.INVALID_PRODUCT_IMAGE
            );
        }

        if (image.getSize() > MAX_FILE_SIZE) {

            log.warn(
                    "Product image validation failed: file size exceeds limit"
            );

            throw new BusinessException(
                    ErrorCode.PRODUCT_IMAGE_SIZE_EXCEEDED
            );
        }

        final String contentType =
                image.getContentType();

        if (contentType == null
                || !contentType
                .toLowerCase()
                .startsWith("image/")) {

            log.warn(
                    "Product image validation failed: invalid content type {}",
                    contentType
            );

            throw new BusinessException(
                    ErrorCode.INVALID_PRODUCT_IMAGE
            );
        }
    }

    private void removeExistingPrimaryImage(
            final String productId
    ) {

        productImageRepository
                .findByProduct_IdAndPrimaryTrue(
                        productId
                )
                .ifPresent(existingPrimary -> {

                    existingPrimary.setPrimary(
                            false
                    );

                    productImageRepository.save(
                            existingPrimary
                    );

                    log.debug(
                            "Existing primary image removed. Image: {}",
                            existingPrimary.getId()
                    );
                });
    }
}