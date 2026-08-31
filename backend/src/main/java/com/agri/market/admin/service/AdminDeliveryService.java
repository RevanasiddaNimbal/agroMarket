package com.agri.market.delivery.service;

import com.agri.market.delivery.dto.DeliveryResponseDto;

import java.util.List;

public interface AdminDeliveryService {

    List<DeliveryResponseDto> getAllDeliveries();

    DeliveryResponseDto getDelivery(
            String deliveryId
    );

    DeliveryResponseDto markAsShipped(
            String deliveryId
    );

    DeliveryResponseDto markAsOutForDelivery(
            String deliveryId
    );
}