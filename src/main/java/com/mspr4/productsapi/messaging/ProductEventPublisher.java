package com.mspr4.productsapi.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.mspr4.productsapi.messaging.ProductEvent.EventType;

@Component
public class ProductEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange = "products.exchange"; // Nom de ton exchange
    private final String routingKey = "products.event";

    public ProductEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishProductCreated(com.mspr4.productsapi.model.Product product) {
        ProductEvent event = new ProductEvent(product, EventType.CREATED);
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }

    public void publishProductUpdated(com.mspr4.productsapi.model.Product product) {
        ProductEvent event = new ProductEvent(product, EventType.UPDATED);
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }

    public void publishProductDeleted(com.mspr4.productsapi.model.Product product) {
        ProductEvent event = new ProductEvent(product, EventType.DELETED);
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
