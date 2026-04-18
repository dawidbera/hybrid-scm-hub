package com.scm.hub.application.service;

import com.scm.hub.domain.model.Order;
import com.scm.hub.domain.port.OrderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderPort orderPort;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_shouldCreateOrder() {
        Order order = Order.builder().customerName("Test").build();
        when(orderPort.createOrder(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrder(order);

        assertThat(result).isNotNull();
        assertThat(result.getCustomerName()).isEqualTo("Test");
        assertThat(result.getStatus()).isEqualTo("Created");
    }
}