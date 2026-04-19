package com.scm.hub.infrastructure.adapter.persistence.mapper;

import com.scm.hub.domain.model.Order;
import com.scm.hub.domain.model.OrderItem;
import com.scm.hub.infrastructure.adapter.persistence.entity.OrderEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for Order entities and domain models.
 * Handles the bidirectional conversion between {@link Order} and {@link OrderEntity}, 
 * including the nested conversion of order items.
 */
@Component
public class OrderMapper {

    /**
     * Converts an Order domain model to an OrderEntity.
     * 
     * @param order The domain model to convert.
     * @return The corresponding JPA entity.
     */
    public OrderEntity toEntity(Order order) {
        OrderEntity entity = OrderEntity.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();

        if (order.getItems() != null) {
            entity.setItems(order.getItems().stream()
                    .map(item -> toItemEntity(item, entity))
                    .collect(Collectors.toList()));
        }
        return entity;
    }

    /**
     * Converts an OrderEntity to an Order domain model.
     * 
     * @param entity The JPA entity to convert.
     * @return The corresponding domain model.
     */
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

    /**
     * Helper method to convert an OrderItem domain model to its entity representation.
     */
    private OrderItemEntity toItemEntity(OrderItem item, OrderEntity orderEntity) {
        return OrderItemEntity.builder()
                .id(item.getId())
                .order(orderEntity)
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }

    /**
     * Helper method to convert an OrderItemEntity to its domain model representation.
     */
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