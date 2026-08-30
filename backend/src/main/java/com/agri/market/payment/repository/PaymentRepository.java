package com.agri.market.payment.repository;

import com.agri.market.payment.entity.Payment;
import com.agri.market.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, String> {

    Optional<Payment> findByOrderId(
            String orderId
    );

    Optional<Payment> findByIdAndOrderUserId(
            String paymentId,
            String userId
    );

    List<Payment> findAllByOrderUserIdOrderByCreatedDateDesc(
            String userId
    );

    List<Payment> findAllByStatusOrderByCreatedDateDesc(
            PaymentStatus status
    );

    boolean existsByOrderId(
            String orderId
    );
}