package com.scm.hub.infrastructure.adapter.rest;

import com.scm.hub.base.AbstractIntegrationTest;
import com.scm.hub.domain.model.Order;
import com.scm.hub.domain.model.OrderStatus;
import com.scm.hub.application.service.OrderService;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the order document download functionality.
 * Verifies that order documents can be retrieved from S3 and are regenerated if missing.
 */
@AutoConfigureMockMvc
public class OrderDocumentDownloadIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private S3Template s3Template;

    @Autowired
    private S3Client s3Client;

    @Value("${scm.aws.s3.bucket-name}")
    private String bucketName;

    /**
     * Ensures the S3 bucket exists before each test.
     */
    @BeforeEach
    void setup() {
        try {
            s3Client.createBucket(b -> b.bucket(bucketName));
        } catch (Exception ignored) {}
    }

    /**
     * Verifies that if an order document is missing from S3, the download request
     * triggers its regeneration and returns the file successfully.
     */
    @Test
    @WithMockUser
    void shouldRegenerateDocumentIfMissingFromS3() throws Exception {
        // Given: An order exists in the database
        Order order = new Order();
        order.setCustomerName("Fallback Test Customer");
        order.setItems(new ArrayList<>());
        Order savedOrder = orderService.createOrder(order);
        UUID orderId = savedOrder.getId();

        // And: The document is manually deleted from S3 to simulate a missing file
        String fileName = String.format("order-%s.json", orderId);
        s3Template.deleteObject(bucketName, fileName);
        assertThat(s3Template.objectExists(bucketName, fileName)).isFalse();

        // When: We request the document via the controller
        // Then: It should return 200 OK because the controller triggers regeneration
        mockMvc.perform(get("/api/orders/{id}/document", orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"order-" + orderId + ".json\""));

        // And: The document should exist in S3 again
        assertThat(s3Template.objectExists(bucketName, fileName)).isTrue();
    }

    /**
     * Verifies that requesting a document for a non-existent order returns a 404 status.
     */
    @Test
    @WithMockUser
    void shouldReturn404IfOrderDoesNotExist() throws Exception {
        // Given: A random UUID that doesn't correspond to any order
        UUID nonExistentId = UUID.randomUUID();

        // When/Then: It should return 404 Not Found
        mockMvc.perform(get("/api/orders/{id}/document", nonExistentId))
                .andExpect(status().isNotFound());
    }
}
