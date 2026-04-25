package com.scm.hub.application.service;

import com.scm.hub.domain.model.Order;
import com.scm.hub.domain.model.OrderStatus;
import com.scm.hub.domain.port.OrderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for OrderService.
 * Validates order lifecycle management and business rules.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderPort orderPort;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderService orderService;

    /**
     * Verifies that a new order is correctly initialized with CREATED status.
     */
    @Test
    void createOrder_shouldCreateOrder() {
        Order order = Order.builder().customerName("Test").build();
        when(orderPort.saveOrder(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrder(order);

        assertThat(result).isNotNull();
        assertThat(result.getCustomerName()).isEqualTo("Test");
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);
    }
}
