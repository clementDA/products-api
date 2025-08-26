package com.mspr4.productsapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mspr4.productsapi.model.Product;
import com.mspr4.productsapi.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest {

    private MockMvc mockMvc;
    private ProductService service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        service = mock(ProductService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(service)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllProducts_shouldReturnList() throws Exception {
        Product p1 = new Product(); p1.setProductId(UUID.randomUUID()); p1.setName("Café");
        Product p2 = new Product(); p2.setProductId(UUID.randomUUID()); p2.setName("Thé");

        when(service.getAllProducts()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllProducts_shouldReturnEmptyList() throws Exception {
        when(service.getAllProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getProductById_shouldReturnProduct() throws Exception {
        UUID id = UUID.randomUUID();
        Product product = new Product(); product.setProductId(id); product.setName("Café");

        when(service.getProductById(id)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Café"));
    }

    @Test
    void getProductById_shouldReturn404IfNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.getProductById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_shouldReturnSavedProduct() throws Exception {
        Product product = new Product();
        product.setName("Café");
        product.setPrice(BigDecimal.valueOf(3.5));
        product.setStockQuantity(10);

        Product saved = new Product();
        saved.setProductId(UUID.randomUUID());
        saved.setName("Café");

        when(service.saveProduct(any(Product.class))).thenReturn(saved);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Café"))
                .andExpect(jsonPath("$.productId").exists());
    }

    @Test
    void createProduct_shouldFailIfNoName() throws Exception {
        Product product = new Product(); // name missing

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct() throws Exception {
        UUID id = UUID.randomUUID();
        Product existing = new Product(); existing.setProductId(id); existing.setName("Café");
        Product update = new Product();
        update.setName("Café Deluxe");
        update.setPrice(BigDecimal.valueOf(4.5));
        update.setStockQuantity(20);

        when(service.getProductById(id)).thenReturn(Optional.of(existing));
        when(service.saveProduct(any(Product.class))).thenReturn(existing);

        mockMvc.perform(put("/api/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Café Deluxe"));
    }

    @Test
    void updateProduct_shouldReturn404IfNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        Product update = new Product();
        update.setName("Café Deluxe"); update.setPrice(BigDecimal.valueOf(4.5)); update.setStockQuantity(10);

        when(service.getProductById(id)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_shouldCallService() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(service).deleteProduct(id);

        mockMvc.perform(delete("/api/products/{id}", id))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteProduct(id);
    }

    @Test
    void deleteProduct_shouldReturn204EvenIfNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new NoSuchElementException()).when(service).deleteProduct(id);

        mockMvc.perform(delete("/api/products/{id}", id))
                .andExpect(status().isNoContent());
    }
}
