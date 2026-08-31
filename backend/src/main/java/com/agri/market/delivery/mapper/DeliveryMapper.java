package com.agri.market.delivery.mapper;

import com.agri.market.delivery.dto.DeliveryResponseDto;
import com.agri.market.delivery.entity.Delivery;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {

    public DeliveryResponseDto toResponseDto(
            final Delivery delivery
    ) {

        return DeliveryResponseDto.builder()
                .id(delivery.getId())
                .orderId(delivery.getOrder().getId())
                .otpVerified(delivery.isOtpVerified())
                .deliveredAt(delivery.getDeliveredAt())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }
}
