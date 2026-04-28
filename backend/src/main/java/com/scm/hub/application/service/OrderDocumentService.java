package com.scm.hub.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scm.hub.domain.model.Order;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Service responsible for generating and storing order documents in AWS S3.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDocumentService {

    private final S3Template s3Template;
    private final ObjectMapper objectMapper;

    @Value("${scm.aws.s3.bucket-name}")
    private String bucketName;

    /**
     * Generates a JSON document for the given order and uploads it to S3.
     *
     * @param order The order to document.
     */
    public void uploadOrderDocument(Order order) {
        try {
            String fileName = String.format("order-%s.json", order.getId());
            byte[] content = objectMapper.writeValueAsBytes(order);
            
            try (InputStream inputStream = new ByteArrayInputStream(content)) {
                s3Template.upload(bucketName, fileName, inputStream);
            }
            
            log.info("Successfully uploaded order document {} to bucket {}", fileName, bucketName);
        } catch (Exception e) {
            log.error("Failed to upload order document for order {}", order.getId(), e);
        }
    }
}
