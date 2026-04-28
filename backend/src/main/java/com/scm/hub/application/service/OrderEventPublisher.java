package com.scm.hub.application.service;

import com.scm.hub.domain.model.Order;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service responsible for publishing order events to AWS SQS.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final SqsTemplate sqsTemplate;

    @Value("${scm.aws.sqs.queue-name}")
    private String queueName;

    /**
     * Publishes a message to SQS indicating a new order has been created.
     *
     * @param order The created order.
     */
    public void publishOrderCreatedEvent(Order order) {
        try {
            sqsTemplate.send(queueName, order);
            log.info("Published order created event to SQS for order ID: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to publish order event to SQS for order {}", order.getId(), e);
        }
    }
}
