package com.mspr4.productsapi.messaging;

import com.mspr4.productsapi.model.Product;
import java.util.UUID;

public class ProductEvent {

    public enum EventType { CREATED, UPDATED, DELETED }

    private UUID productId;
    private String name;
    private EventType eventType;

    public ProductEvent(Product product, EventType eventType) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.eventType = eventType;
    }

    public UUID getProductId() { return productId; }
    public String getName() { return name; }
    public EventType getEventType() { return eventType; }
}
