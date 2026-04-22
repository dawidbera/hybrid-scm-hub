package com.scm.hub.infrastructure.adapter.sync;

import com.scm.hub.domain.model.OrderStatus;
import com.scm.hub.infrastructure.adapter.persistence.entity.ProductEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.SyncLogEntity;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.ProductRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.SyncLogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "sync.enabled=false",
    "spring.main.allow-bean-definition-overriding=true"
})
@Testcontainers
class SyncIntegrationTest {

    @Container
    static PostgreSQLContainer<?> onPremDb = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("scm_onprem")
            .withUsername("scm_user")
            .withPassword("scm_password");

    @Container
    static PostgreSQLContainer<?> cloudDb = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("scm_cloud")
            .withUsername("scm_user")
            .withPassword("scm_password");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.onprem.url", onPremDb::getJdbcUrl);
        registry.add("spring.datasource.onprem.username", onPremDb::getUsername);
        registry.add("spring.datasource.onprem.password", onPremDb::getPassword);

        registry.add("spring.datasource.cloud.url", cloudDb::getJdbcUrl);
        registry.add("spring.datasource.cloud.username", cloudDb::getUsername);
        registry.add("spring.datasource.cloud.password", cloudDb::getPassword);
    }

    @Autowired
    private SyncService syncService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SyncLogRepository syncLogRepository;

    @PersistenceContext(unitName = "cloud")
    private EntityManager cloudEntityManager;

    @Test
    void shouldSyncProductToCloud() {
        // Given
        ProductEntity product = ProductEntity.builder()
                .sku("TEST-SKU-001")
                .name("Integration Test Product")
                .description("Test Description")
                .basePrice(BigDecimal.valueOf(99.99))
                .build();
        product = productRepository.save(product);

        SyncLogEntity syncLog = SyncLogEntity.builder()
                .entityName("Product")
                .entityId(product.getId())
                .status(OrderStatus.PENDING)
                .syncTimestamp(LocalDateTime.now())
                .build();
        syncLog = syncLogRepository.save(syncLog);

        // When
        syncService.syncEntity(syncLog);

        // Then
        SyncLogEntity updatedLog = syncLogRepository.findById(syncLog.getId()).orElseThrow();
        assertThat(updatedLog.getStatus())
                .withFailMessage("Sync failed with error: " + updatedLog.getErrorMessage())
                .isEqualTo(OrderStatus.SUCCESS);

        ProductEntity cloudProduct = cloudEntityManager.find(ProductEntity.class, product.getId());
        assertThat(cloudProduct).isNotNull();
        assertThat(cloudProduct.getSku()).isEqualTo("TEST-SKU-001");
    }
}
