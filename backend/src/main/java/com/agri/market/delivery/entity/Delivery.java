package com.agri.market.delivery.entity;

import com.agri.market.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "deliveries",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_delivery_order",
                        columnNames = "order_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_deliveries_order_id",
                        columnList = "order_id"
                ),
                @Index(
                        name = "idx_deliveries_otp_expires_at",
                        columnList = "otp_expires_at"
                )
        }
)
public class Delivery {

    @Id
    @Column(
            name = "id",
            nullable = false,
            length = 36
    )
    private String id;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "order_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(
                    name = "fk_delivery_order"
            )
    )
    private Order order;

    @Column(
            name = "otp",
            length = 6
    )
    private String otp;

    @Column(
            name = "otp_expires_at"
    )
    private LocalDateTime otpExpiresAt;

    @Column(
            name = "otp_verified",
            nullable = false
    )
    @Builder.Default
    private boolean otpVerified = false;

    @Column(
            name = "delivered_at"
    )
    private LocalDateTime deliveredAt;

    @Column(
            name = "failure_reason",
            length = 500
    )
    private String failureReason;

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

        if (id == null) {
            id = UUID.randomUUID().toString();
        }

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
