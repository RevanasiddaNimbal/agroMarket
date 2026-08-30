package com.agri.market.admin.service;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.payment.dto.PaymentTransactionResponseDto;
import com.agri.market.payment.entity.PaymentStatus;
import com.agri.market.payment.entity.TransactionType;
import com.agri.market.payment.mapper.PaymentTransactionMapper;
import com.agri.market.payment.repository.PaymentRepository;
import com.agri.market.payment.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminPaymentTransactionServiceImpl
        implements AdminPaymentTransactionService {

    private final PaymentTransactionRepository
            paymentTransactionRepository;

    private final PaymentRepository paymentRepository;

    private final PaymentTransactionMapper
            paymentTransactionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionResponseDto> getAllTransactions() {

        log.info(
                "Admin fetching all payment transactions"
        );

        return paymentTransactionRepository
                .findAll()
                .stream()
                .map(paymentTransactionMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionResponseDto> getTransactionsByType(
            final TransactionType transactionType
    ) {

        log.info(
                "Admin fetching transactions by type: {}",
                transactionType
        );

        return paymentTransactionRepository
                .findAllByTransactionTypeOrderByCreatedDateDesc(
                        transactionType
                )
                .stream()
                .map(paymentTransactionMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionResponseDto> getTransactionsByStatus(
            final PaymentStatus status
    ) {

        log.info(
                "Admin fetching transactions by status: {}",
                status
        );

        return paymentTransactionRepository
                .findAllByStatusOrderByCreatedDateDesc(
                        status
                )
                .stream()
                .map(paymentTransactionMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionResponseDto> getPaymentTransactions(
            final String paymentId
    ) {

        log.info(
                "Admin fetching transactions for payment: {}",
                paymentId
        );

        paymentRepository
                .findById(paymentId)
                .orElseThrow(() -> {
                    log.warn(
                            "Payment not found for admin transaction lookup: {}",
                            paymentId
                    );

                    return new BusinessException(
                            ErrorCode.PAYMENT_NOT_FOUND
                    );
                });

        return paymentTransactionRepository
                .findAllByPaymentIdOrderByCreatedDateDesc(
                        paymentId
                )
                .stream()
                .map(paymentTransactionMapper::toResponseDto)
                .toList();
    }
}