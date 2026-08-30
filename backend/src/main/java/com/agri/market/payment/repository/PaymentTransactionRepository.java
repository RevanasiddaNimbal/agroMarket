package com.agri.market.payment.repository;

import com.agri.market.payment.entity.PaymentStatus;
import com.agri.market.payment.entity.PaymentTransaction;
import com.agri.market.payment.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentTransactionRepository
        extends JpaRepository<PaymentTransaction, String> {

    List<PaymentTransaction>
    findAllByPaymentIdOrderByCreatedDateDesc(
            String paymentId
    );

    List<PaymentTransaction>
    findAllByOrderUserIdOrderByCreatedDateDesc(
            String userId
    );

    List<PaymentTransaction>
    findAllByOrderItemsProductFarmerIdOrderByCreatedDateDesc(
            String farmerId
    );

    List<PaymentTransaction>
    findAllByTransactionTypeOrderByCreatedDateDesc(
            TransactionType transactionType
    );

    List<PaymentTransaction>
    findAllByStatusOrderByCreatedDateDesc(
            PaymentStatus status
    );

    boolean existsByProviderTransactionId(
            String providerTransactionId
    );
}