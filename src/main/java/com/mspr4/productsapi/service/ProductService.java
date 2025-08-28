package com.mspr4.productsapi.service;

import com.mspr4.productsapi.model.Product;
import com.mspr4.productsapi.repository.ProductRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.mspr4.productsapi.config.RabbitConfig.PRODUCT_QUEUE;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public ProductService(ProductRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Optional<Product> getProductById(UUID id) {
        return repository.findById(id);
    }

    public Product saveProduct(Product product) {
        Product saved = repository.save(product);

        rabbitTemplate.convertAndSend(PRODUCT_QUEUE, "Product saved: " + saved.getProductId());
        return saved;
    }

    public void deleteProduct(UUID id) {
        repository.deleteById(id);
        rabbitTemplate.convertAndSend(PRODUCT_QUEUE, "Product deleted: " + id);
    }
}
