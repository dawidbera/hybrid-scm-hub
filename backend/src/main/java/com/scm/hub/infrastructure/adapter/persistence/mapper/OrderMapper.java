package com.scm.hub.infrastructure.adapter.persistence.mapper;

import com.scm.hub.domain.model.Order;
import com.scm.hub.domain.model.OrderItem;
import com.scm.hub.infrastructure.adapter.persistence.entity.OrderEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for Order entities and domain models.
 */
@Component
public class OrderMapper {

    public OrderEntity toEntity(Order order) {
        return OrderEntity.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .status(order.getStatus())
                .items(order.getItems() != null ? order.getItems().stream()
                        .map(this::toItemEntity)
                        .collect(Collectors.toList()) : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public Order toDomain(OrderEntity entity) {
        return Order.builder()
                .id(entity.getId())
                .customerName(entity.getCustomerName())
                .status(entity.getStatus())
                .items(entity.getItems() != null ? entity.getItems().stream()
                        .map(this::toItemDomain)
                        .collect(Collectors.toList()) : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private OrderItemEntity toItemEntity(OrderItem item) {
        return OrderItemEntity.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }

    private OrderItem toItemDomain(OrderItemEntity entity) {
        return OrderItem.builder()
                .id(entity.getId())
                .orderId(entity.getOrder().getId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .build();
    }
}