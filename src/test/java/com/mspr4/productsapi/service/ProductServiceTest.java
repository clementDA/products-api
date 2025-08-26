package com.mspr4.productsapi.service;

import com.mspr4.productsapi.model.Product;
import com.mspr4.productsapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository repository;
    private ProductService service;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        service = new ProductService(repository);
    }

    @Test
    void saveProduct_shouldReturnSavedProduct() {
        Product product = new Product();
        product.setName("Café");
        product.setPrice(BigDecimal.valueOf(3.5));
        product.setStockQuantity(10);

        when(repository.save(product)).thenReturn(product);

        Product saved = service.saveProduct(product);

        assertNotNull(saved);
        assertEquals("Café", saved.getName());
        verify(repository, times(1)).save(product);
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
}
