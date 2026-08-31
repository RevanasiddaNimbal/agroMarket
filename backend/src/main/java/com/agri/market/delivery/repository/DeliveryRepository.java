package com.agri.market.delivery.repository;

import com.agri.market.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository
        extends JpaRepository<Delivery, String> {

    Optional<Delivery> findByOrderId(
            String orderId
    );

    boolean existsByOrderId(
            String orderId
    );
}