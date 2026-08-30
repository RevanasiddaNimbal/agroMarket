package com.agri.market.order.repository;

import com.agri.market.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, String> {

    List<OrderItem> findAllByOrderId(
            String orderId
    );

    List<OrderItem> findAllByProductId(
            String productId
    );
}