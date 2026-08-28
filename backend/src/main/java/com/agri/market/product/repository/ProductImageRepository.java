package com.agri.market.product.repository;

import com.agri.market.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository
        extends JpaRepository<ProductImage, String> {

    List<ProductImage> findAllByProduct_IdOrderByDisplayOrderAsc(
            String productId
    );

    Optional<ProductImage> findByIdAndProduct_Id(
            String imageId,
            String productId
    );

    Optional<ProductImage> findByProduct_IdAndPrimaryTrue(
            String productId
    );

    boolean existsByIdAndProduct_Id(
            String imageId,
            String productId
    );

    long countByProduct_Id(
            String productId
    );
}