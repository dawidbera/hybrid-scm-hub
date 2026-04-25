package com.scm.hub.base;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base class for integration tests requiring a dual-database setup (On-Premise and Cloud).
 * Uses the Singleton Container pattern to share database instances across the entire test suite,
 * improving performance and avoiding port conflicts.
 */
@SpringBootTest(properties = {
    "sync.enabled=false",
    "spring.main.allow-bean-definition-overriding=true"
})
public abstract class AbstractIntegrationTest {

    /** Shared PostgreSQL container for the On-Premise database simulation. */
    private static final PostgreSQLContainer<?> ON_PREM_DB = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("scm_onprem")
            .withUsername("scm_user")
            .withPassword("scm_password");

    /** Shared PostgreSQL container for the Cloud database simulation. */
    private static final PostgreSQLContainer<?> CLOUD_DB = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("scm_cloud")
            .withUsername("scm_user")
            .withPassword("scm_password");

    static {
        ON_PREM_DB.start();
        CLOUD_DB.start();
    }

    /**
     * Dynamically registers database connection properties based on the Testcontainers' allocated ports.
     * @param registry The Spring DynamicPropertyRegistry to register properties into.
     */
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.onprem.url", ON_PREM_DB::getJdbcUrl);
        registry.add("spring.datasource.onprem.username", ON_PREM_DB::getUsername);
        registry.add("spring.datasource.onprem.password", ON_PREM_DB::getPassword);

        registry.add("spring.datasource.cloud.url", CLOUD_DB::getJdbcUrl);
        registry.add("spring.datasource.cloud.username", CLOUD_DB::getUsername);
        registry.add("spring.datasource.cloud.password", CLOUD_DB::getPassword);
    }
}
