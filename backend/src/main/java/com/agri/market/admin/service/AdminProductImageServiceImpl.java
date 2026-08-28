package com.agri.market.admin.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminProductImageServiceImpl
        implements AdminProductImageService {

    private static final long MAX_FILE_SIZE =
            5 * 1024 * 1024;

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductImageMapper productImageMapper;

    @Override
    @Transactional
    public ProductImageResponseDto uploadImage(
            final String productId,
            final ProductImageRequestDto request
    ) {

        log.info(
                "Admin uploading product image. Product: {}",
                productId
        );

        final Product product =
                getProduct(productId);

        validateImage(request.getImage());

        final String imageUrl =
                cloudinaryService.uploadProductImage(
                        request.getImage()
                );

        if (request.isPrimary()) {
            removeExistingPrimaryImage(productId);
        }

        final ProductImage productImage =
                ProductImage.builder()
                        .product(product)
                        .imageUrl(imageUrl)
                        .primary(request.isPrimary())
                        .displayOrder(request.getDisplayOrder())
                        .build();

        final ProductImage savedImage =
                productImageRepository.save(productImage);

        log.info(
                "Admin successfully uploaded product image. Product: {}, Image: {}",
                productId,
                savedImage.getId()
        );

        return productImageMapper.toResponseDto(savedImage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageResponseDto> getProductImages(
            final String productId
    ) {

        log.debug(
                "Admin fetching product images. Product: {}",
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
            final ProductImageRequestDto request
    ) {

        log.info(
                "Admin updating product image. Product: {}, Image: {}",
                productId,
                imageId
        );

        getProduct(productId);

        final ProductImage productImage =
                getProductImage(
                        productId,
                        imageId
                );

        if (request.getImage() != null
                && !request.getImage().isEmpty()) {

            validateImage(request.getImage());

            final String imageUrl =
                    cloudinaryService.uploadProductImage(
                            request.getImage()
                    );

            productImage.setImageUrl(imageUrl);
        }

        if (request.isPrimary()) {
            removeExistingPrimaryImage(productId);
        }

        productImage.setPrimary(
                request.isPrimary()
        );

        productImage.setDisplayOrder(
                request.getDisplayOrder()
        );

        final ProductImage updatedImage =
                productImageRepository.save(productImage);

        log.info(
                "Admin successfully updated product image. Image: {}",
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
            final String imageId
    ) {

        log.info(
                "Admin deleting product image. Product: {}, Image: {}",
                productId,
                imageId
        );

        getProduct(productId);

        final ProductImage productImage =
                getProductImage(
                        productId,
                        imageId
                );

        productImageRepository.delete(productImage);

        log.info(
                "Admin successfully deleted product image. Image: {}",
                imageId
        );
    }

    @Override
    @Transactional
    public ProductImageResponseDto setPrimaryImage(
            final String productId,
            final String imageId
    ) {

        log.info(
                "Admin setting primary product image. Product: {}, Image: {}",
                productId,
                imageId
        );

        getProduct(productId);

        final ProductImage productImage =
                getProductImage(
                        productId,
                        imageId
                );

        removeExistingPrimaryImage(productId);

        productImage.setPrimary(true);

        final ProductImage savedImage =
                productImageRepository.save(productImage);

        log.info(
                "Admin successfully set primary image. Image: {}",
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

    private ProductImage getProductImage(
            final String productId,
            final String imageId
    ) {

        return productImageRepository
                .findByIdAndProduct_Id(
                        imageId,
                        productId
                )
                .orElseThrow(() -> {
                    log.warn(
                            "Product image not found. Product: {}, Image: {}",
                            productId,
                            imageId
                    );

                    return new BusinessException(
                            ErrorCode.PRODUCT_IMAGE_NOT_FOUND
                    );
                });
    }

    private void removeExistingPrimaryImage(
            final String productId
    ) {

        productImageRepository
                .findByProduct_IdAndPrimaryTrue(
                        productId
                )
                .ifPresent(existingPrimary -> {

                    existingPrimary.setPrimary(false);

                    productImageRepository.save(
                            existingPrimary
                    );

                    log.debug(
                            "Existing primary image unset. Image: {}",
                            existingPrimary.getId()
                    );
                });
    }

    private void validateImage(
            final MultipartFile image
    ) {

        if (image == null || image.isEmpty()) {

            log.warn(
                    "Admin product image validation failed: empty image"
            );

            throw new BusinessException(
                    ErrorCode.INVALID_PRODUCT_IMAGE
            );
        }

        if (image.getSize() > MAX_FILE_SIZE) {

            log.warn(
                    "Admin product image validation failed: image exceeds 5 MB"
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
                    "Admin product image validation failed: content type {}",
                    contentType
            );

            throw new BusinessException(
                    ErrorCode.INVALID_PRODUCT_IMAGE
            );
        }
    }
}