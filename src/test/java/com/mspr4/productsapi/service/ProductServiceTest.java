package com.mspr4.productsapi.service;

import com.mspr4.productsapi.model.Product;
import com.mspr4.productsapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository repository;
    private RabbitTemplate rabbitTemplate;
    private ProductService service;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        rabbitTemplate = mock(RabbitTemplate.class);
        service = new ProductService(repository, rabbitTemplate);
    }

    @Test
    void saveProduct_shouldReturnSavedProduct_andPublishEvent() {
        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(10));
        product.setStockQuantity(5);

        when(repository.save(product)).thenReturn(product);

        Product saved = service.saveProduct(product);

        verify(repository, times(1)).save(product);
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("productQueue"), eq("Product saved: " + saved.getProductId()));
    }

    @Test
    void getProductById_shouldReturnProductIfExists() {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setProductId(id);

        when(repository.findById(id)).thenReturn(Optional.of(product));

        Optional<Product> found = service.getProductById(id);

        assertTrue(found.isPresent());
        assertEquals(id, found.get().getProductId());
    }

    @Test
    void deleteProduct_shouldCallRepositoryAndPublishEvent() {
        UUID id = UUID.randomUUID();

        service.deleteProduct(id);

        verify(repository, times(1)).deleteById(id);
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("productQueue"), eq("Product deleted: " + id));
    }
}
