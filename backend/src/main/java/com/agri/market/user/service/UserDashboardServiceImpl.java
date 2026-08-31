package com.agri.market.user.service;

import com.agri.market.address.repository.AddressRepository;
import com.agri.market.inventory.repository.InventoryRepository;
import com.agri.market.order.entity.OrderStatus;
import com.agri.market.order.repository.OrderRepository;
import com.agri.market.product.entity.ProductStatus;
import com.agri.market.product.repository.ProductRepository;
import com.agri.market.user.dto.UserDashboardBuyingDto;
import com.agri.market.user.dto.UserDashboardResponseDto;
import com.agri.market.user.dto.UserDashboardSellingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDashboardServiceImpl
        implements UserDashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDashboardResponseDto getDashboard(
            final String userId
    ) {

        log.info(
                "Fetching dashboard for user: {}",
                userId
        );

        final long totalOrders =
                orderRepository
                        .findAllByUserIdOrderByCreatedDateDesc(userId)
                        .size();

        final long pendingPaymentOrders =
                orderRepository
                        .findAllByStatusOrderByCreatedDateDesc(
                                OrderStatus.PENDING_PAYMENT
                        )
                        .stream()
                        .filter(order ->
                                order.getUser()
                                        .getId()
                                        .equals(userId)
                        )
                        .count();

        final long deliveredOrders =
                orderRepository
                        .findAllByStatusOrderByCreatedDateDesc(
                                OrderStatus.DELIVERED
                        )
                        .stream()
                        .filter(order ->
                                order.getUser()
                                        .getId()
                                        .equals(userId)
                        )
                        .count();

        final long activeOrders =
                orderRepository
                        .findAllByUserIdOrderByCreatedDateDesc(userId)
                        .stream()
                        .filter(order ->
                                order.getStatus() == OrderStatus.CONFIRMED
                                        || order.getStatus() == OrderStatus.PROCESSING
                                        || order.getStatus() == OrderStatus.SHIPPED
                                        || order.getStatus() == OrderStatus.OUT_FOR_DELIVERY
                        )
                        .count();

        final long totalProducts =
                productRepository
                        .findAll()
                        .stream()
                        .filter(product ->
                                product.getFarmer()
                                        .getId()
                                        .equals(userId)
                        )
                        .count();

        final long activeProducts =
                productRepository
                        .countByFarmer_IdAndStatus(
                                userId,
                                ProductStatus.ACTIVE.name()
                        );

        final long totalProductOrders =
                orderRepository
                        .findAllByItemsProductFarmerIdOrderByCreatedDateDesc(
                                userId
                        )
                        .size();

        final long inventoryItems =
                inventoryRepository
                        .findAllByFarmerId(userId)
                        .size();

        final long totalAddresses =
                addressRepository
                        .findAllByUserId(userId)
                        .size();

        final UserDashboardBuyingDto buying =
                UserDashboardBuyingDto.builder()
                        .totalOrders(totalOrders)
                        .pendingPaymentOrders(
                                pendingPaymentOrders
                        )
                        .activeOrders(activeOrders)
                        .deliveredOrders(deliveredOrders)
                        .build();

        final UserDashboardSellingDto selling =
                UserDashboardSellingDto.builder()
                        .totalProducts(totalProducts)
                        .activeProducts(activeProducts)
                        .totalProductOrders(
                                totalProductOrders
                        )
                        .inventoryItems(inventoryItems)
                        .build();

        return UserDashboardResponseDto.builder()
                .buying(buying)
                .selling(selling)
                .totalAddresses(totalAddresses)
                .build();
    }
}