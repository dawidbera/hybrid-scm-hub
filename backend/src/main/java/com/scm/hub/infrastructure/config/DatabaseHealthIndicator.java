package com.scm.hub.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Custom Health Indicator to monitor the connectivity status of both On-Premise and Cloud databases.
 * Provides detailed health status for each datasource in the /actuator/health endpoint.
 */
@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource onPremDataSource;
    private final DataSource cloudDataSource;

    /**
     * Aggregates the health status of both databases.
     * @return Health status with details for both on-prem and cloud connections.
     */
    @Override
    public Health health() {
        Health onPremHealth = checkConnection(onPremDataSource, "On-Premise Database");
        Health cloudHealth = checkConnection(cloudDataSource, "Cloud Database");

        if (onPremHealth.getStatus().equals(org.springframework.boot.actuate.health.Status.UP) &&
            cloudHealth.getStatus().equals(org.springframework.boot.actuate.health.Status.UP)) {
            return Health.up()
                    .withDetail("onPremDatabase", onPremHealth.getDetails())
                    .withDetail("cloudDatabase", cloudHealth.getDetails())
                    .build();
        } else {
            return Health.down()
                    .withDetail("onPremDatabase", onPremHealth.getDetails())
                    .withDetail("cloudDatabase", cloudHealth.getDetails())
                    .build();
        }
    }

    /**
     * Helper method to verify the connectivity of a specific datasource.
     * @param dataSource The datasource to check.
     * @param dbName Friendly name for logging/reporting.
     * @return Health object for the specific connection.
     */
    private Health checkConnection(DataSource dataSource, String dbName) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("SELECT 1");
            return Health.up().withDetail("message", dbName + " is reachable").build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("message", dbName + " is UNREACHABLE")
                    .withException(e)
                    .build();
        }
    }
}
