package com.agri.market.admin.service;

import com.agri.market.order.dto.OrderResponseDto;
import com.agri.market.order.dto.OrderStatusUpdateRequestDto;

import java.util.List;

public interface AdminOrderService {

    List<OrderResponseDto> getAllOrders();

    OrderResponseDto getOrder(
            String orderId
    );

    OrderResponseDto updateOrderStatus(
            String orderId,
            OrderStatusUpdateRequestDto request
    );
}