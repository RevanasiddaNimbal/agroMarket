package com.agri.market.order.service;

import com.agri.market.address.entity.Address;
import com.agri.market.address.repository.AddressRepository;
import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.inventory.entity.Inventory;
import com.agri.market.inventory.repository.InventoryRepository;
import com.agri.market.order.dto.OrderResponseDto;
import com.agri.market.order.dto.OrderStatusUpdateRequestDto;
import com.agri.market.order.dto.PlaceOrderRequestDto;
import com.agri.market.order.entity.Order;
import com.agri.market.order.entity.OrderItem;
import com.agri.market.order.entity.OrderStatus;
import com.agri.market.order.mapper.OrderMapper;
import com.agri.market.order.repository.OrderRepository;
import com.agri.market.payment.service.PaymentService;
import com.agri.market.product.entity.Product;
import com.agri.market.product.entity.ProductStatus;
import com.agri.market.product.repository.ProductRepository;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final PaymentService paymentService;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(
            final PlaceOrderRequestDto request,
            final String userId
    ) {

        log.info(
                "Placing order. User: {}, Product: {}, Quantity: {}",
                userId,
                request.getProductId(),
                request.getQuantity()
        );

        final User user =
                userRepository.findById(userId)
                        .orElseThrow(() -> {
                            log.warn(
                                    "User not found while placing order: {}",
                                    userId
                            );

                            return new BusinessException(
                                    ErrorCode.USER_NOT_FOUND
                            );
                        });

        final Product product =
                productRepository.findById(request.getProductId())
                        .orElseThrow(() -> {
                            log.warn(
                                    "Product not found while placing order: {}",
                                    request.getProductId()
                            );

                            return new BusinessException(
                                    ErrorCode.PRODUCT_NOT_FOUND
                            );
                        });

        validateProduct(product);

        final Address address =
                addressRepository.findByIdAndUserId(
                                request.getAddressId(),
                                userId
                        )
                        .orElseThrow(() -> {
                            log.warn(
                                    "Address not found or does not belong to user. Address: {}, User: {}",
                                    request.getAddressId(),
                                    userId
                            );

                            return new BusinessException(
                                    ErrorCode.ADDRESS_NOT_FOUND
                            );
                        });

        final Inventory inventory =
                inventoryRepository.findByProductIdForUpdate(
                                product.getId()
                        )
                        .orElseThrow(() -> {
                            log.warn(
                                    "Inventory not found while placing order. Product: {}",
                                    product.getId()
                            );

                            return new BusinessException(
                                    ErrorCode.INVENTORY_NOT_FOUND
                            );
                        });

        final BigDecimal productQuantity =
                product.getQuantity() == null
                        ? BigDecimal.ZERO
                        : product.getQuantity();

        final BigDecimal reservedQuantity =
                inventory.getReservedQuantity() == null
                        ? BigDecimal.ZERO
                        : inventory.getReservedQuantity();

        final BigDecimal availableQuantity =
                productQuantity.subtract(reservedQuantity);

        if (request.getQuantity().compareTo(availableQuantity) > 0) {

            log.warn(
                    "Order rejected due to insufficient stock. Product: {}, Requested: {}, Available: {}",
                    product.getId(),
                    request.getQuantity(),
                    availableQuantity
            );

            throw new BusinessException(
                    ErrorCode.INVENTORY_INSUFFICIENT_STOCK
            );
        }

        final BigDecimal unitPrice =
                product.getPrice();

        final BigDecimal subtotal =
                unitPrice.multiply(
                        request.getQuantity()
                );

        final Order order =
                Order.builder()
                        .user(user)
                        .address(address)
                        .status(OrderStatus.PENDING_PAYMENT)
                        .totalAmount(subtotal)
                        .build();

        final OrderItem orderItem =
                OrderItem.builder()
                        .order(order)
                        .product(product)
                        .quantity(request.getQuantity())
                        .unitPrice(unitPrice)
                        .subtotal(subtotal)
                        .build();

        order.getItems().add(orderItem);

        final Order savedOrder =
                orderRepository.save(order);

        log.info(
                "Order placed successfully. Order: {}, User: {}, Total: {}",
                savedOrder.getId(),
                userId,
                savedOrder.getTotalAmount()
        );

        return orderMapper.toResponseDto(
                savedOrder
        );
    }

    private void validateProduct(
            final Product product
    ) {

        if (!ProductStatus.ACTIVE.name().equals(product.getStatus())) {

            log.warn(
                    "Order rejected because product is not active: {}",
                    product.getId()
            );

            throw new BusinessException(
                    ErrorCode.PRODUCT_NOT_AVAILABLE
            );
        }

        if (product.getPrice() == null
                || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {

            log.warn(
                    "Order rejected because product price is invalid: {}",
                    product.getId()
            );

            throw new BusinessException(
                    ErrorCode.PRODUCT_NOT_AVAILABLE
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(
            final String orderId,
            final String userId
    ) {

        log.info(
                "Fetching order: {} for user: {}",
                orderId,
                userId
        );

        final Order order =
                orderRepository.findByIdAndUserId(
                        orderId,
                        userId
                ).orElseThrow(() -> {

                    log.warn(
                            "Order not found or does not belong to user. Order: {}, User: {}",
                            orderId,
                            userId
                    );

                    return new BusinessException(
                            ErrorCode.ORDER_NOT_FOUND
                    );
                });

        return orderMapper.toResponseDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getMyOrders(
            final String userId
    ) {

        log.info(
                "Fetching orders for user: {}",
                userId
        );

        return orderRepository
                .findAllByUserIdOrderByCreatedDateDesc(userId)
                .stream()
                .map(orderMapper::toResponseDto)
                .toList();
    }
    
    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(
            final String orderId,
            final String userId,
            final OrderStatusUpdateRequestDto request
    ) {

        log.info(
                "Updating order status. Order: {}, User: {}, Status: {}",
                orderId,
                userId,
                request.getStatus()
        );

        final Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Order not found: {}",
                                    orderId
                            );

                            return new BusinessException(
                                    ErrorCode.ORDER_NOT_FOUND
                            );
                        });

        validateSellerAccess(
                order,
                userId
        );

        validateStatusTransition(
                order.getStatus(),
                request.getStatus()
        );

        order.setStatus(
                request.getStatus()
        );

        final Order updatedOrder =
                orderRepository.save(order);

        log.info(
                "Order status updated successfully. Order: {}, Status: {}",
                orderId,
                request.getStatus()
        );

        return orderMapper.toResponseDto(
                updatedOrder
        );
    }

    private void validateSellerAccess(
            final Order order,
            final String userId
    ) {

        final boolean belongsToUser =
                order.getItems()
                        .stream()
                        .anyMatch(orderItem ->
                                orderItem.getProduct()
                                        .getFarmer()
                                        .getId()
                                        .equals(userId)
                        );

        if (!belongsToUser) {

            log.warn(
                    "User {} attempted to modify order {} without seller ownership",
                    userId,
                    order.getId()
            );

            throw new BusinessException(
                    ErrorCode.ORDER_ACCESS_DENIED
            );
        }
    }

    private void validateStatusTransition(
            final OrderStatus currentStatus,
            final OrderStatus newStatus
    ) {

        if (currentStatus == OrderStatus.DELIVERED
                || currentStatus == OrderStatus.CANCELLED) {

            log.warn(
                    "Invalid order status transition from {} to {}",
                    currentStatus,
                    newStatus
            );

            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_STATUS_TRANSITION
            );
        }

        if (newStatus == OrderStatus.CANCELLED) {

            if (currentStatus != OrderStatus.CONFIRMED) {

                throw new BusinessException(
                        ErrorCode.ORDER_INVALID_STATUS_TRANSITION
                );
            }

            return;
        }

        if (currentStatus == OrderStatus.CONFIRMED
                && newStatus != OrderStatus.PROCESSING) {

            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_STATUS_TRANSITION
            );
        }

        if (currentStatus == OrderStatus.PROCESSING
                && newStatus != OrderStatus.SHIPPED) {

            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_STATUS_TRANSITION
            );
        }

        if (currentStatus == OrderStatus.SHIPPED
                && newStatus != OrderStatus.OUT_FOR_DELIVERY) {

            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_STATUS_TRANSITION
            );
        }

        if (currentStatus == OrderStatus.OUT_FOR_DELIVERY
                && newStatus != OrderStatus.DELIVERED) {

            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_STATUS_TRANSITION
            );
        }
    }

    @Override
    @Transactional
    public void cancelOrder(
            final String orderId,
            final String userId
    ) {

        log.info(
                "Cancelling order: {} for user: {}",
                orderId,
                userId
        );

        final Order order =
                orderRepository.findByIdAndUserId(
                        orderId,
                        userId
                ).orElseThrow(() -> {

                    log.warn(
                            "Order not found or does not belong to user. Order: {}, User: {}",
                            orderId,
                            userId
                    );

                    return new BusinessException(
                            ErrorCode.ORDER_NOT_FOUND
                    );
                });

        validateCancellation(order);

        order.setStatus(
                OrderStatus.CANCELLED
        );

        orderRepository.save(order);

        log.info(
                "Order marked as CANCELLED. Order: {}",
                orderId
        );

        paymentService.refundPayment(
                orderId,
                userId
        );

        log.info(
                "Order cancellation and payment refund completed successfully. Order: {}",
                orderId
        );
    }

    private void validateCancellation(
            final Order order
    ) {
        final OrderStatus status = order.getStatus();

        if (status != OrderStatus.PENDING_PAYMENT
                && status != OrderStatus.CONFIRMED
                && status != OrderStatus.PROCESSING) {

            log.warn(
                    "Order cancellation rejected. Order: {}, Current status: {}",
                    order.getId(),
                    status
            );

            throw new BusinessException(
                    ErrorCode.ORDER_INVALID_STATUS_TRANSITION
            );
        }
    }
}
