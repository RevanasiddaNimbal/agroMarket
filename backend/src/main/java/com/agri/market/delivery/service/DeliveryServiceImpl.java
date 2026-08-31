package com.agri.market.delivery.service;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.delivery.dto.DeliveryOtpRequestDto;
import com.agri.market.delivery.dto.DeliveryOtpVerificationRequestDto;
import com.agri.market.delivery.dto.DeliveryResponseDto;
import com.agri.market.delivery.entity.Delivery;
import com.agri.market.delivery.mapper.DeliveryMapper;
import com.agri.market.delivery.repository.DeliveryRepository;
import com.agri.market.email.service.EmailService;
import com.agri.market.order.entity.Order;
import com.agri.market.order.entity.OrderStatus;
import com.agri.market.order.repository.OrderRepository;
import com.agri.market.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    private static final int OTP_EXPIRY_MINUTES = 10;

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final DeliveryMapper deliveryMapper;
    private final EmailService emailService;

    private final SecureRandom secureRandom =
            new SecureRandom();

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponseDto getDelivery(
            final String orderId,
            final String userId
    ) {

        final Order order =
                orderRepository.findByIdAndUserId(
                        orderId,
                        userId
                ).orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.ORDER_NOT_FOUND
                        )
                );

        final Delivery delivery =
                deliveryRepository.findByOrderId(
                        order.getId()
                ).orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.DELIVERY_NOT_FOUND
                        )
                );

        return deliveryMapper.toResponseDto(
                delivery
        );
    }

    @Override
    @Transactional
    public DeliveryResponseDto createDelivery(
            final Order order
    ) {

        log.info(
                "Creating delivery for order: {}",
                order.getId()
        );

        if (deliveryRepository.existsByOrderId(order.getId())) {

            log.warn(
                    "Delivery already exists for order: {}",
                    order.getId()
            );

            throw new BusinessException(
                    ErrorCode.DELIVERY_ALREADY_EXISTS
            );
        }

        final Delivery delivery =
                Delivery.builder()
                        .order(order)
                        .otpVerified(false)
                        .build();

        final Delivery savedDelivery =
                deliveryRepository.save(delivery);

        log.info(
                "Delivery created successfully for order: {}",
                order.getId()
        );

        return deliveryMapper.toResponseDto(
                savedDelivery
        );
    }

    @Override
    @Transactional
    public void generateDeliveryOtp(
            final DeliveryOtpRequestDto request,
            final String userId
    ) {

        final Order order =
                orderRepository.findByIdAndUserId(
                        request.getOrderId(),
                        userId
                ).orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.ORDER_NOT_FOUND
                        )
                );

        validateOtpGeneration(order);

        final Delivery delivery =
                deliveryRepository.findByOrderId(
                        order.getId()
                ).orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.DELIVERY_NOT_FOUND
                        )
                );

        final String otp =
                generateOtp();

        delivery.setOtp(otp);

        delivery.setOtpExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(OTP_EXPIRY_MINUTES)
        );

        delivery.setOtpVerified(false);
        delivery.setDeliveredAt(null);
        delivery.setFailureReason(null);

        deliveryRepository.save(delivery);

        final User user =
                order.getUser();

        emailService.sendDeliveryOtpEmail(
                user.getEmail(),
                otp
        );

        log.info(
                "Delivery OTP generated for order: {}",
                otp
        );
    }

    @Override
    @Transactional
    public DeliveryResponseDto verifyDeliveryOtp(
            final DeliveryOtpVerificationRequestDto request,
            final String userId
    ) {

        final Order order =
                orderRepository.findByIdAndUserId(
                        request.getOrderId(),
                        userId
                ).orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.ORDER_NOT_FOUND
                        )
                );

        if (order.getStatus()
                != OrderStatus.OUT_FOR_DELIVERY) {

            throw new BusinessException(
                    ErrorCode.DELIVERY_NOT_AVAILABLE
            );
        }

        final Delivery delivery =
                deliveryRepository.findByOrderId(
                        order.getId()
                ).orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.DELIVERY_NOT_FOUND
                        )
                );

        validateOtp(
                delivery,
                request.getOtp()
        );

        delivery.setOtpVerified(true);
        delivery.setOtp(null);
        delivery.setOtpExpiresAt(null);
        delivery.setDeliveredAt(
                LocalDateTime.now()
        );

        order.setStatus(
                OrderStatus.DELIVERED
        );

        deliveryRepository.save(delivery);
        orderRepository.save(order);

        log.info(
                "Delivery completed successfully for order: {}",
                order.getId()
        );

        return deliveryMapper.toResponseDto(
                delivery
        );
    }

    private void validateOtpGeneration(
            final Order order
    ) {

        if (order.getStatus()
                != OrderStatus.OUT_FOR_DELIVERY) {

            throw new BusinessException(
                    ErrorCode.DELIVERY_NOT_AVAILABLE
            );
        }
    }

    private void validateOtp(
            final Delivery delivery,
            final String otp
    ) {

        if (delivery.isOtpVerified()) {

            throw new BusinessException(
                    ErrorCode.DELIVERY_OTP_ALREADY_VERIFIED
            );
        }

        if (delivery.getOtp() == null
                || delivery.getOtpExpiresAt() == null) {

            throw new BusinessException(
                    ErrorCode.DELIVERY_OTP_NOT_FOUND
            );
        }

        if (delivery.getOtpExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new BusinessException(
                    ErrorCode.DELIVERY_OTP_EXPIRED
            );
        }

        if (!delivery.getOtp().equals(otp)) {

            throw new BusinessException(
                    ErrorCode.DELIVERY_OTP_INVALID
            );
        }
    }

    private String generateOtp() {

        return String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );
    }
}