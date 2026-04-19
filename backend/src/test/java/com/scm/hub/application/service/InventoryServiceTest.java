package com.scm.hub.application.service;

import com.scm.hub.domain.model.Product;
import com.scm.hub.domain.model.Stock;
import com.scm.hub.domain.model.Warehouse;
import com.scm.hub.domain.port.InventoryPort;
import com.scm.hub.infrastructure.adapter.rest.WebSocketController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryPort inventoryPort;

    @Mock
    private WebSocketController webSocketController;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void updateStock_shouldValidateExistingProductAndWarehouse() {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();

        when(inventoryPort.findProductById(productId)).thenReturn(Optional.of(Product.builder().id(productId).sku("S-001").build()));
        when(inventoryPort.findWarehouseById(warehouseId)).thenReturn(Optional.of(Warehouse.builder().id(warehouseId).name("Main").location("Poland").build()));
        when(inventoryPort.saveStock(any(Stock.class))).thenAnswer(invocation -> {
            Stock stock = invocation.getArgument(0);
            stock.setId(UUID.randomUUID());
            stock.setLastUpdated(LocalDateTime.now());
            return stock;
        });

        Stock updated = inventoryService.updateStock(productId, warehouseId, 42);

        assertThat(updated).isNotNull();
        assertThat(updated.getQuantity()).isEqualTo(42);
        assertThat(updated.getProductId()).isEqualTo(productId);
        assertThat(updated.getWarehouseId()).isEqualTo(warehouseId);
    }

    @Test
    void updateStock_shouldRejectNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> inventoryService.updateStock(UUID.randomUUID(), UUID.randomUUID(), -1));
    }
}
