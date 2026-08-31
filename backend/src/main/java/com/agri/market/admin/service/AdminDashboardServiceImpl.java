package com.agri.market.admin.service;

import com.agri.market.admin.dto.AdminDashboardResponseDto;
import com.agri.market.delivery.repository.DeliveryRepository;
import com.agri.market.inventory.repository.InventoryRepository;
import com.agri.market.order.repository.OrderRepository;
import com.agri.market.payment.repository.PaymentRepository;
import com.agri.market.payment.repository.PaymentTransactionRepository;
import com.agri.market.product.repository.ProductRepository;
import com.agri.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardServiceImpl
        implements AdminDashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final InventoryRepository inventoryRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponseDto getDashboard() {

        log.info("Fetching admin dashboard");

        return AdminDashboardResponseDto.builder()
                .totalUsers(userRepository.count())
                .totalProducts(productRepository.count())
                .totalOrders(orderRepository.count())
                .totalPayments(paymentRepository.count())
                .totalPaymentTransactions(
                        paymentTransactionRepository.count()
                )
                .totalInventory(inventoryRepository.count())
                .totalDeliveries(deliveryRepository.count())
                .build();
    }
}