package com.agri.market.admin.service;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.order.dto.OrderResponseDto;
import com.agri.market.order.dto.OrderStatusUpdateRequestDto;
import com.agri.market.order.entity.Order;
import com.agri.market.order.entity.OrderStatus;
import com.agri.market.order.mapper.OrderMapper;
import com.agri.market.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {

        log.info("Fetching all orders for admin");

        return orderRepository
                .findAll()
                .stream()
                .map(orderMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(
            final String orderId
    ) {

        log.info(
                "Fetching order for admin: {}",
                orderId
        );

        final Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Order not found for admin: {}",
                                    orderId
                            );

                            return new BusinessException(
                                    ErrorCode.ORDER_NOT_FOUND
                            );
                        });

        return orderMapper.toResponseDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(
            final String orderId,
            final OrderStatusUpdateRequestDto request
    ) {

        log.info(
                "Admin updating order status. Order: {}, Status: {}",
                orderId,
                request.getStatus()
        );

        final Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Order not found for admin status update: {}",
                                    orderId
                            );

                            return new BusinessException(
                                    ErrorCode.ORDER_NOT_FOUND
                            );
                        });

        validateStatusTransition(
                order.getStatus(),
                request.getStatus()
        );

        order.setStatus(request.getStatus());

        final Order updatedOrder =
                orderRepository.save(order);

        log.info(
                "Admin updated order status successfully. Order: {}, Status: {}",
                orderId,
                request.getStatus()
        );

        return orderMapper.toResponseDto(updatedOrder);
    }

    private void validateStatusTransition(
            final OrderStatus currentStatus,
            final OrderStatus newStatus
    ) {

        if (currentStatus == OrderStatus.DELIVERED
                || currentStatus == OrderStatus.CANCELLED) {

            log.warn(
                    "Invalid admin order status transition from {} to {}",
                    currentStatus,
                    newStatus
            );

            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_STATUS_TRANSITION
            );
        }

        if (newStatus == OrderStatus.CANCELLED) {

            if (currentStatus != OrderStatus.CONFIRMED) {
                throw new BusinessException(
                        ErrorCode.ORDER_INVALID_STATUS_TRANSITION
                );
            }

            return;
        }

        if (currentStatus == OrderStatus.CONFIRMED
                && newStatus != OrderStatus.PROCESSING) {

            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_STATUS_TRANSITION
            );
        }

        if (currentStatus == OrderStatus.PROCESSING
                && newStatus != OrderStatus.SHIPPED) {

            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_STATUS_TRANSITION
            );
        }

        if (currentStatus == OrderStatus.SHIPPED
                && newStatus != OrderStatus.OUT_FOR_DELIVERY) {

            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_STATUS_TRANSITION
            );
        }

        if (currentStatus == OrderStatus.OUT_FOR_DELIVERY
                && newStatus != OrderStatus.DELIVERED) {

            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_STATUS_TRANSITION
            );
        }
    }
}