package com.agri.market.order.repository;

import com.agri.market.order.entity.Order;
import com.agri.market.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface OrderRepository
        extends JpaRepository<Order, String>,
        JpaSpecificationExecutor<Order> {

    List<Order> findAllByUserIdOrderByCreatedDateDesc(
            String userId
    );

    Optional<Order> findByIdAndUserId(
            String orderId,
            String userId
    );

    List<Order> findAllByItemsProductFarmerIdOrderByCreatedDateDesc(
            String farmerId
    );

    List<Order> findAllByStatusOrderByCreatedDateDesc(
            OrderStatus status
    );
}