package com.scm.hub.infrastructure.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for the data sources used in the hybrid-cloud environment.
 * This class sets up two separate entity managers and transaction managers for:
 * 1. On-Premise database (Primary)
 * 2. Cloud database simulation
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.scm.hub.infrastructure.adapter.persistence.repository.onprem",
    entityManagerFactoryRef = "onPremEntityManagerFactory",
    transactionManagerRef = "onPremTransactionManager"
)
public class DataSourceConfig {

    /**
     * Configuration properties for the On-Premise data source.
     * @return DataSourceProperties for the on-prem database.
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.onprem")
    public DataSourceProperties onPremDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * The primary data source representing the On-Premise environment.
     * @return The On-Premise DataSource.
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.onprem")
    public DataSource onPremDataSource() {
        return onPremDataSourceProperties().initializeDataSourceBuilder().build();
    }

    /**
     * Entity Manager Factory for the On-Premise persistence unit.
     * @param builder The factory builder.
     * @return LocalContainerEntityManagerFactoryBean for the on-prem database.
     */
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean onPremEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(onPremDataSource())
                .packages("com.scm.hub.infrastructure.adapter.persistence.entity")
                .persistenceUnit("onprem")
                .build();
    }

    /**
     * Transaction manager for the On-Premise environment.
     * @param onPremEntityManagerFactory The on-prem entity manager factory.
     * @return PlatformTransactionManager for the on-prem database.
     */
    @Bean
    @Primary
    public PlatformTransactionManager onPremTransactionManager(
            LocalContainerEntityManagerFactoryBean onPremEntityManagerFactory) {
        return new JpaTransactionManager(onPremEntityManagerFactory.getObject());
    }

    /**
     * Configuration properties for the Cloud data source simulation.
     * @return DataSourceProperties for the cloud database.
     */
    @Bean
    @ConfigurationProperties("spring.datasource.cloud")
    public DataSourceProperties cloudDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Data source representing the Cloud environment simulation.
     * @return The Cloud DataSource.
     */
    @Bean
    public DataSource cloudDataSource() {
        return cloudDataSourceProperties().initializeDataSourceBuilder().build();
    }

    /**
     * Entity Manager Factory for the Cloud persistence unit.
     * @param builder The factory builder.
     * @return LocalContainerEntityManagerFactoryBean for the cloud database.
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean cloudEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(cloudDataSource())
                .packages("com.scm.hub.infrastructure.adapter.persistence.entity")
                .persistenceUnit("cloud")
                .build();
    }

    /**
     * Transaction manager for the Cloud environment simulation.
     * @param cloudEntityManagerFactory The cloud entity manager factory.
     * @return PlatformTransactionManager for the cloud database.
     */
    @Bean
    public PlatformTransactionManager cloudTransactionManager(
            LocalContainerEntityManagerFactoryBean cloudEntityManagerFactory) {
        return new JpaTransactionManager(cloudEntityManagerFactory.getObject());
    }
}
