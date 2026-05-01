package com.scm.hub.infrastructure.adapter.sync;

import com.scm.hub.base.AbstractIntegrationTest;
import com.scm.hub.domain.model.Order;
import com.scm.hub.domain.model.OrderStatus;
import com.scm.hub.application.service.OrderService;
import io.awspring.cloud.s3.S3Template;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration test for cloud-native order operations.
 * Verifies that creating an order correctly triggers S3 document generation and SQS event publication.
 */
public class OrderCloudNativeIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private S3Template s3Template;

    @Autowired
    private SqsTemplate sqsTemplate;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private SqsAsyncClient sqsAsyncClient;

    @Value("${scm.aws.s3.bucket-name}")
    private String bucketName;

    @Value("${scm.aws.sqs.queue-name}")
    private String queueName;

    /**
     * Initializes the necessary AWS infrastructure (S3 bucket and SQS queue) in LocalStack.
     */
    @BeforeEach
    void setup() {
        // Create bucket and queue if they don't exist in LocalStack
        try {
            s3Client.createBucket(b -> b.bucket(bucketName));
        } catch (Exception ignored) {}
        
        try {
            sqsAsyncClient.createQueue(b -> b.queueName(queueName)).join();
        } catch (Exception ignored) {}
    }

    /**
     * Verifies that order creation results in an asynchronous document upload to S3
     * and a notification message sent to SQS.
     */
    @Test
    void shouldCreateOrderAndTriggerCloudNativeOperations() {
        // Given
        Order order = new Order();
        order.setCustomerName("Test Customer");
        order.setItems(new ArrayList<>()); // Empty items for simplicity in this test

        // When
        Order savedOrder = orderService.createOrder(order);

        // Then: Verify S3 Document
        String expectedFileName = String.format("order-%s.json", savedOrder.getId());
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            assertThat(s3Template.objectExists(bucketName, expectedFileName)).isTrue();
        });

        // Then: Verify SQS Event
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            var message = sqsTemplate.receive(queueName, Order.class);
            assertThat(message).isPresent();
            assertThat(message.get().getPayload().getId()).isEqualTo(savedOrder.getId());
        });
    }
}
