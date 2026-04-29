package com.scm.hub.base;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

/**
 * Base class for integration tests requiring a dual-database setup (On-Premise and Cloud)
 * and AWS service simulation (S3, SQS).
...
 */
@SpringBootTest(properties = {
    "sync.enabled=false",
    "spring.main.allow-bean-definition-overriding=true"
})
@org.springframework.test.context.ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    private static final PostgreSQLContainer<?> ON_PREM_DB = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("scm_onprem")
            .withUsername("scm_user")
            .withPassword("scm_password");

    private static final PostgreSQLContainer<?> CLOUD_DB = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("scm_cloud")
            .withUsername("scm_user")
            .withPassword("scm_password");

    private static final LocalStackContainer LOCALSTACK = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.4"))
            .withServices(S3, SQS);

    static {
        ON_PREM_DB.start();
        CLOUD_DB.start();
        LOCALSTACK.start();
    }

    /**
     * Dynamically registers database and AWS connection properties.
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

        // LocalStack properties
        registry.add("spring.cloud.aws.s3.endpoint", () -> LOCALSTACK.getEndpointOverride(S3).toString());
        registry.add("spring.cloud.aws.sqs.endpoint", () -> LOCALSTACK.getEndpointOverride(SQS).toString());
        registry.add("spring.cloud.aws.region.static", LOCALSTACK::getRegion);
        registry.add("spring.cloud.aws.credentials.access-key", LOCALSTACK::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", LOCALSTACK::getSecretKey);
    }
}
