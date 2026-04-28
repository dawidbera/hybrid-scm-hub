package com.scm.hub.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.scm.hub.infrastructure.adapter.persistence.repository.cloud",
    entityManagerFactoryRef = "cloudEntityManagerFactory",
    transactionManagerRef = "cloudTransactionManager"
)
public class CloudRepositoryConfig {
}
