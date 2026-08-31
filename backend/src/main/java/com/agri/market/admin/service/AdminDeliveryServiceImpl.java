package com.agri.market.admin.service;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.delivery.dto.DeliveryResponseDto;
import com.agri.market.delivery.entity.Delivery;
import com.agri.market.delivery.mapper.DeliveryMapper;
import com.agri.market.delivery.repository.DeliveryRepository;
import com.agri.market.delivery.service.AdminDeliveryService;
import com.agri.market.order.entity.Order;
import com.agri.market.order.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDeliveryServiceImpl
        implements AdminDeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDto> getAllDeliveries() {

        log.info("Fetching all deliveries for admin");

        return deliveryRepository
                .findAll()
                .stream()
                .map(deliveryMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponseDto getDelivery(
            final String deliveryId
    ) {

        log.info(
                "Fetching delivery: {} for admin",
                deliveryId
        );

        final Delivery delivery =
                deliveryRepository.findById(deliveryId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Delivery not found: {}",
                                    deliveryId
                            );

                            return new BusinessException(
                                    ErrorCode.DELIVERY_NOT_FOUND
                            );
                        });

        return deliveryMapper.toResponseDto(
                delivery
        );
    }

    @Override
    @Transactional
    public DeliveryResponseDto markAsShipped(
            final String deliveryId
    ) {

        log.info(
                "Marking delivery as SHIPPED: {}",
                deliveryId
        );

        final Delivery delivery =
                findDelivery(deliveryId);

        final Order order =
                delivery.getOrder();

        validateStatus(
                order,
                OrderStatus.PROCESSING,
                OrderStatus.SHIPPED
        );

        order.setStatus(
                OrderStatus.SHIPPED
        );

        log.info(
                "Order marked as SHIPPED. Order: {}",
                order.getId()
        );

        return deliveryMapper.toResponseDto(
                delivery
        );
    }

    @Override
    @Transactional
    public DeliveryResponseDto markAsOutForDelivery(
            final String deliveryId
    ) {

        log.info(
                "Marking delivery as OUT_FOR_DELIVERY: {}",
                deliveryId
        );

        final Delivery delivery =
                findDelivery(deliveryId);

        final Order order =
                delivery.getOrder();

        validateStatus(
                order,
                OrderStatus.SHIPPED,
                OrderStatus.OUT_FOR_DELIVERY
        );

        order.setStatus(
                OrderStatus.OUT_FOR_DELIVERY
        );

        log.info(
                "Order marked as OUT_FOR_DELIVERY. Order: {}",
                order.getId()
        );

        return deliveryMapper.toResponseDto(
                delivery
        );
    }

    private Delivery findDelivery(
            final String deliveryId
    ) {

        return deliveryRepository
                .findById(deliveryId)
                .orElseThrow(() -> {

                    log.warn(
                            "Delivery not found: {}",
                            deliveryId
                    );

                    return new BusinessException(
                            ErrorCode.DELIVERY_NOT_FOUND
                    );
                });
    }

    private void validateStatus(
            final Order order,
            final OrderStatus expectedStatus,
            final OrderStatus newStatus
    ) {

        if (order.getStatus() != expectedStatus) {

            log.warn(
                    "Invalid delivery status transition. Order: {}, Current: {}, Expected: {}, Requested: {}",
                    order.getId(),
                    order.getStatus(),
                    expectedStatus,
                    newStatus
            );

            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_STATUS_TRANSITION
            );
        }
    }
}