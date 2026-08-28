package com.agri.market.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "product_images",
        indexes = {
                @Index(
                        name = "idx_product_images_product_id",
                        columnList = "product_id"
                ),
                @Index(
                        name = "idx_product_images_product_primary",
                        columnList = "product_id, is_primary"
                ),
                @Index(
                        name = "idx_product_images_product_order",
                        columnList = "product_id, display_order"
                )
        }
)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = UUID)
    @Column(
            nullable = false,
            updatable = false
    )
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;

    @Column(
            name = "image_url",
            nullable = false,
            length = 1000
    )
    private String imageUrl;

    @Column(
            name = "is_primary",
            nullable = false
    )
    @Builder.Default
    private boolean primary = false;

    @Column(
            name = "display_order",
            nullable = false
    )
    @Builder.Default
    private int displayOrder = 0;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}