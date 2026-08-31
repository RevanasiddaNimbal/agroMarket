package com.agri.market.delivery.service;

import com.agri.market.delivery.dto.DeliveryOtpRequestDto;
import com.agri.market.delivery.dto.DeliveryOtpVerificationRequestDto;
import com.agri.market.delivery.dto.DeliveryResponseDto;
import com.agri.market.order.entity.Order;

public interface DeliveryService {

    DeliveryResponseDto getDelivery(
            String orderId,
            String userId
    );

    void generateDeliveryOtp(
            DeliveryOtpRequestDto request,
            String userId
    );

    DeliveryResponseDto verifyDeliveryOtp(
            DeliveryOtpVerificationRequestDto request,
            String userId
    );

    DeliveryResponseDto createDelivery(
            Order order
    );
}

