package com.mercadolibre.product_api.repository;

import com.mercadolibre.product_api.model.CreateProduct;
import com.mercadolibre.product_api.model.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest {

    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ProductRepository();
    }

    @Test
    void save_NewProduct_SavesSuccessfully() {
        CreateProduct product = createTestProduct();
        CreateProduct savedProduct = repository.save(product);

        assertNotNull(savedProduct.getId());
        assertEquals(product.getTitle(), savedProduct.getTitle());
    }

    @Test
    void findById_ExistingProduct_ReturnsProduct() {
        CreateProduct product = repository.save(createTestProduct());
        Optional<CreateProduct> found = repository.findById(product.getId());

        assertTrue(found.isPresent());
        assertEquals(product.getId(), found.get().getId());
    }

    @Test
    void findById_NonExistingProduct_ReturnsEmpty() {
        Optional<CreateProduct> found = repository.findById("non-existing-id");
        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_ReturnsAllProducts() {
        repository.save(createTestProduct());
        repository.save(createTestProduct());

        List<CreateProduct> products = repository.findAll();
        assertFalse(products.isEmpty());
        assertTrue(products.size() >= 2);
    }

    @Test
    void deleteById_ExistingProduct_DeletesSuccessfully() {
        CreateProduct product = repository.save(createTestProduct());
        repository.deleteById(product.getId());

        Optional<CreateProduct> found = repository.findById(product.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void existsById_ExistingProduct_ReturnsTrue() {
        CreateProduct product = repository.save(createTestProduct());
        assertTrue(repository.existsById(product.getId()));
    }

    @Test
    void existsById_NonExistingProduct_ReturnsFalse() {
        assertFalse(repository.existsById("non-existing-id"));
    }

    @Test
    void update_ExistingProduct_UpdatesSuccessfully() {
        CreateProduct product = repository.save(createTestProduct());
        product.setTitle("Updated Title");
        
        CreateProduct updated = repository.save(product);
        
        assertEquals("Updated Title", updated.getTitle());
        assertEquals(product.getId(), updated.getId());
    }

    private CreateProduct createTestProduct() {
        return CreateProduct.builder()
                .title("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .seller(Seller.builder()
                    .name("Test Seller")
                    .isOfficialStore(true)
                    .build())
                .build();
    }
} 