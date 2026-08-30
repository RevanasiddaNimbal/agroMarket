package com.agri.market.admin.service;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.payment.dto.PaymentResponseDto;
import com.agri.market.payment.entity.Payment;
import com.agri.market.payment.entity.PaymentStatus;
import com.agri.market.payment.mapper.PaymentMapper;
import com.agri.market.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminPaymentServiceImpl
        implements AdminPaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getAllPayments() {

        log.info("Admin fetching all payments");

        return paymentRepository
                .findAll()
                .stream()
                .map(paymentMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(
            final String paymentId
    ) {

        log.info(
                "Admin fetching payment: {}",
                paymentId
        );

        final Payment payment =
                paymentRepository.findById(paymentId)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Payment not found for admin: {}",
                                    paymentId
                            );

                            return new BusinessException(
                                    ErrorCode.PAYMENT_NOT_FOUND
                            );
                        });

        return paymentMapper.toResponseDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByStatus(
            final PaymentStatus status
    ) {

        log.info(
                "Admin fetching payments by status: {}",
                status
        );

        return paymentRepository
                .findAllByStatusOrderByCreatedDateDesc(status)
                .stream()
                .map(paymentMapper::toResponseDto)
                .toList();
    }
}