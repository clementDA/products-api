package com.mspr4.productsapi.controller;

import com.mspr4.productsapi.model.Product;
import com.mspr4.productsapi.service.ProductService;
import com.mspr4.productsapi.messaging.ProductEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;
    private final ProductEventPublisher eventPublisher;

    public ProductController(ProductService service, ProductEventPublisher eventPublisher) {
        this.service = service;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping
    public List<Product> getAll() {
        return service.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable UUID id) {
        return service.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        Product saved = service.saveProduct(product);
        eventPublisher.publishProductCreated(saved);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable UUID id,
                                          @Valid @RequestBody Product product) {
        return service.getProductById(id)
                .map(existing -> {
                    existing.setName(product.getName());
                    existing.setDescription(product.getDescription());
                    existing.setPrice(product.getPrice());
                    existing.setStockQuantity(product.getStockQuantity());
                    existing.setImageUrl(product.getImageUrl());
                    Product updated = service.saveProduct(existing);
                    eventPublisher.publishProductUpdated(updated);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.getProductById(id).ifPresent(product -> {
            service.deleteProduct(id);
            eventPublisher.publishProductDeleted(product);
        });
        return ResponseEntity.noContent().build(); // 204
    }
}
