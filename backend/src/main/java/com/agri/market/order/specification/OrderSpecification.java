package com.agri.market.order.specification;

import com.agri.market.order.entity.Order;
import com.agri.market.order.entity.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

public final class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<Order> belongsToUser(
            final String userId
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("user").get("id"),
                        userId
                );
    }

    public static Specification<Order> hasStatus(
            final OrderStatus status
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("status"),
                        status
                );
    }

    public static Specification<Order> containsProductFromFarmer(
            final String farmerId
    ) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            return criteriaBuilder.equal(
                    root.join("items")
                            .get("product")
                            .get("farmer")
                            .get("id"),
                    farmerId
            );
        };
    }
}