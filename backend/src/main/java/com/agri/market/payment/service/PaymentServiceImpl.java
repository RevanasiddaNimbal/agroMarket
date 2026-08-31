package com.agri.market.payment.service;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.delivery.service.DeliveryService;
import com.agri.market.order.entity.Order;
import com.agri.market.order.entity.OrderStatus;
import com.agri.market.order.repository.OrderRepository;
import com.agri.market.payment.dto.PaymentRequestDto;
import com.agri.market.payment.dto.PaymentResponseDto;
import com.agri.market.payment.dto.RefundResponseDto;
import com.agri.market.payment.entity.Payment;
import com.agri.market.payment.entity.PaymentStatus;
import com.agri.market.payment.entity.PaymentTransaction;
import com.agri.market.payment.entity.TransactionType;
import com.agri.market.payment.mapper.PaymentMapper;
import com.agri.market.payment.provider.PaymentProvider;
import com.agri.market.payment.provider.PaymentProviderFactory;
import com.agri.market.payment.provider.PaymentProviderResponse;
import com.agri.market.payment.repository.PaymentRepository;
import com.agri.market.payment.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentProviderFactory paymentProviderFactory;
    private final DeliveryService deliveryService;

    @Override
    @Transactional
    public PaymentResponseDto processPayment(
            final String orderId,
            final String userId,
            final PaymentRequestDto request
    ) {

        log.info(
                "Processing payment. Order: {}, User: {}",
                orderId,
                userId
        );

        final Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Order not found for payment: {}",
                                    orderId
                            );

                            return new BusinessException(
                                    ErrorCode.ORDER_NOT_FOUND
                            );
                        });

        if (!order.getUser().getId().equals(userId)) {

            log.warn(
                    "User {} attempted payment for order {} without ownership",
                    userId,
                    orderId
            );

            throw new BusinessException(
                    ErrorCode.ORDER_ACCESS_DENIED
            );
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {

            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_STATUS_TRANSITION
            );
        }

        if (paymentRepository.existsByOrderId(orderId)) {

            log.warn(
                    "Payment already exists for order: {}",
                    orderId
            );

            throw new BusinessException(
                    ErrorCode.PAYMENT_ALREADY_EXISTS
            );
        }

        final PaymentProvider provider =
                paymentProviderFactory.getProvider();

        final PaymentProviderResponse providerResponse =
                provider.processPayment(
                        orderId,
                        order.getTotalAmount(),
                        request.getPaymentMethod()
                );

        order.setStatus(
                providerResponse.isSuccessful()
                        ? OrderStatus.CONFIRMED
                        : OrderStatus.CANCELLED
        );

        orderRepository.save(order);

        final Payment payment =
                Payment.builder()
                        .order(order)
                        .amount(order.getTotalAmount())
                        .paymentMethod(request.getPaymentMethod())
                        .status(
                                providerResponse.isSuccessful()
                                        ? PaymentStatus.SUCCESS
                                        : PaymentStatus.FAILED
                        )
                        .provider(
                                providerResponse.getProvider()
                        )
                        .providerPaymentId(
                                providerResponse.getProviderPaymentId()
                        )
                        .paidAt(
                                providerResponse.isSuccessful()
                                        ? LocalDateTime.now()
                                        : null
                        )
                        .build();

        final Payment savedPayment =
                paymentRepository.save(payment);

        final PaymentTransaction transaction =
                PaymentTransaction.builder()
                        .payment(savedPayment)
                        .order(order)
                        .transactionType(TransactionType.PAYMENT)
                        .amount(order.getTotalAmount())
                        .status(
                                providerResponse.isSuccessful()
                                        ? PaymentStatus.SUCCESS
                                        : PaymentStatus.FAILED
                        )
                        .provider(
                                providerResponse.getProvider()
                        )
                        .providerTransactionId(
                                providerResponse.getProviderTransactionId()
                        )
                        .build();

        paymentTransactionRepository.save(
                transaction
        );

        if (!providerResponse.isSuccessful()) {

            log.warn(
                    "Payment failed. Order: {}, Payment: {}",
                    orderId,
                    savedPayment.getId()
            );

            throw new BusinessException(
                    ErrorCode.PAYMENT_FAILED
            );
        }

        deliveryService.createDelivery(
                order
        );

        log.info(
                "Payment completed successfully and delivery created. Order: {}, Payment: {}",
                orderId,
                savedPayment.getId()
        );

        return paymentMapper.toResponseDto(
                savedPayment
        );
    }

    @Override
    @Transactional
    public RefundResponseDto refundPayment(
            final String orderId,
            final String userId
    ) {

        log.info(
                "Processing refund. Order: {}, User: {}",
                orderId,
                userId
        );

        final Payment payment =
                paymentRepository.findByOrderId(orderId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Payment not found for order: {}",
                                    orderId
                            );

                            return new BusinessException(
                                    ErrorCode.PAYMENT_NOT_FOUND
                            );
                        });

        final Order order =
                payment.getOrder();

        if (!order.getUser().getId().equals(userId)) {

            log.warn(
                    "User {} attempted refund for order {} without ownership",
                    userId,
                    orderId
            );

            throw new BusinessException(
                    ErrorCode.ORDER_ACCESS_DENIED
            );
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {

            log.warn(
                    "Refund rejected. Payment: {}, Status: {}",
                    payment.getId(),
                    payment.getStatus()
            );

            throw new BusinessException(
                    ErrorCode.PAYMENT_REFUND_NOT_ALLOWED
            );
        }

        if (order.getStatus() != OrderStatus.CANCELLED) {

            log.warn(
                    "Refund rejected because order is not cancelled. Order: {}, Status: {}",
                    orderId,
                    order.getStatus()
            );

            throw new BusinessException(
                    ErrorCode.PAYMENT_REFUND_NOT_ALLOWED
            );
        }

        final PaymentProvider provider =
                paymentProviderFactory.getProvider();

        final PaymentProviderResponse providerResponse =
                provider.processRefund(
                        payment.getProviderPaymentId(),
                        payment.getAmount()
                );

        if (!providerResponse.isSuccessful()) {

            log.warn(
                    "Refund failed. Payment: {}",
                    payment.getId()
            );

            throw new BusinessException(
                    ErrorCode.PAYMENT_REFUND_FAILED
            );
        }

        payment.setStatus(
                PaymentStatus.REFUNDED
        );

        payment.setRefundedAt(
                LocalDateTime.now()
        );

        paymentRepository.save(payment);

        final PaymentTransaction refundTransaction =
                PaymentTransaction.builder()
                        .payment(payment)
                        .order(order)
                        .transactionType(TransactionType.REFUND)
                        .amount(payment.getAmount())
                        .status(PaymentStatus.REFUNDED)
                        .provider(
                                providerResponse.getProvider()
                        )
                        .providerTransactionId(
                                providerResponse.getProviderTransactionId()
                        )
                        .build();

        paymentTransactionRepository.save(
                refundTransaction
        );

        log.info(
                "Refund completed successfully. Order: {}, Payment: {}",
                orderId,
                payment.getId()
        );

        return paymentMapper.toRefundResponseDto(
                payment
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(
            final String orderId,
            final String userId
    ) {

        log.info(
                "Fetching payment for order: {}, User: {}",
                orderId,
                userId
        );

        final Payment payment =
                paymentRepository.findByOrderId(
                        orderId
                ).orElseThrow(() -> {

                    log.warn(
                            "Payment not found for order: {}",
                            orderId
                    );

                    return new BusinessException(
                            ErrorCode.PAYMENT_NOT_FOUND
                    );
                });

        if (!payment.getOrder()
                .getUser()
                .getId()
                .equals(userId)) {

            log.warn(
                    "User {} attempted to access payment for order {} without ownership",
                    userId,
                    orderId
            );

            throw new BusinessException(
                    ErrorCode.ORDER_ACCESS_DENIED
            );
        }

        return paymentMapper.toResponseDto(
                payment
        );
    }
}