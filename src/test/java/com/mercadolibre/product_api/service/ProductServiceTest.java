package com.mercadolibre.product_api.service;

import com.mercadolibre.product_api.dto.PagedResponse;
import com.mercadolibre.product_api.dto.ProductSearchParams;
import com.mercadolibre.product_api.dto.UpdateProductRequest;
import com.mercadolibre.product_api.exception.ProductNotFoundException;
import com.mercadolibre.product_api.model.CreateProduct;
import com.mercadolibre.product_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.mercadolibre.product_api.dto.CreateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.mercadolibre.product_api.model.Seller;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private CreateProduct testProduct;
    private List<CreateProduct> testProducts;

    @BeforeEach
    void setUp() {
        testProduct = CreateProduct.builder()
                .id("MLB1234567")
                .title("Test Product")
                .description("Test Description")
                .price(new BigDecimal("100.00"))
                .build();

        CreateProduct anotherProduct = CreateProduct.builder()
                .id("MLB7654321")
                .title("Another Product")
                .description("Another Description")
                .price(new BigDecimal("200.00"))
                .build();

        testProducts = Arrays.asList(testProduct, anotherProduct);
    }

    @Test
    void getProductById_ExistingProduct_ReturnsProduct() {
        when(productRepository.findById("MLB1234567")).thenReturn(Optional.of(testProduct));

        CreateProduct result = productService.getProductById("MLB1234567");

        assertNotNull(result);
        assertEquals("MLB1234567", result.getId());
        assertEquals("Test Product", result.getTitle());
        verify(productRepository).findById("MLB1234567");
    }

    @Test
    void getProductById_NonExistingProduct_ThrowsException() {
        when(productRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.getProductById("nonexistent"));
        verify(productRepository).findById("nonexistent");
    }

    @Test
    void searchProducts_WithDefaultParams_ReturnsAllProducts() {
        when(productRepository.findAll()).thenReturn(testProducts);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        ProductSearchParams params = ProductSearchParams.builder().build();
        Page<CreateProduct> result = productService.searchProducts(params, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        assertTrue(result.isFirst());
        verify(productRepository).findAll();
    }

    @Test
    void searchProducts_WithPriceFilter_ReturnsFilteredProducts() {
        when(productRepository.findAll()).thenReturn(testProducts);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        ProductSearchParams params = ProductSearchParams.builder()
                .minPrice(new BigDecimal("150.00"))
                .build();

        Page<CreateProduct> result = productService.searchProducts(params, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("MLB7654321", result.getContent().get(0).getId());
        verify(productRepository).findAll();
    }

    @Test
    void searchProducts_WithSearchQuery_ReturnsMatchingProducts() {
        when(productRepository.findAll()).thenReturn(testProducts);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        ProductSearchParams params = ProductSearchParams.builder()
                .query("Another")
                .build();

        Page<CreateProduct> result = productService.searchProducts(params, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("MLB7654321", result.getContent().get(0).getId());
        verify(productRepository).findAll();
    }

    @Test
    void searchProducts_WithStoreName_ReturnsMatchingProducts() {
        // Configuramos el vendedor del producto
        Seller seller1 = Seller.builder()
                .id("SELLER1")
                .storeName("Tienda Electrónica")
                .isOfficialStore(true)
                .build();
        
        Seller seller2 = Seller.builder()
                .id("SELLER2")
                .storeName("Tienda de Ropa")
                .isOfficialStore(false)
                .build();

        // Configuramos los productos con sus respectivos vendedores
        testProduct.setSeller(seller1);
        CreateProduct anotherProduct = CreateProduct.builder()
                .id("MLB7654321")
                .title("Another Product")
                .description("Another Description")
                .price(new BigDecimal("200.00"))
                .seller(seller2)
                .build();

        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct, anotherProduct));

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        ProductSearchParams params = ProductSearchParams.builder()
                .storeName("Electrónica")
                .build();

        Page<CreateProduct> result = productService.searchProducts(params, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Tienda Electrónica", result.getContent().get(0).getSeller().getStoreName());
        verify(productRepository).findAll();
    }

    @Test
    void updateProduct_WithValidData_UpdatesSuccessfully() {
        String productId = "MLB1234567";
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(CreateProduct.class))).thenAnswer(i -> i.getArguments()[0]);

        UpdateProductRequest request = UpdateProductRequest.builder()
                .title("Updated Title")
                .price(new BigDecimal("150.00"))
                .build();

        CreateProduct result = productService.updateProduct(productId, request);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals(new BigDecimal("150.00"), result.getPrice());
        assertEquals(testProduct.getDescription(), result.getDescription());
        verify(productRepository).findById(productId);
        verify(productRepository).save(any(CreateProduct.class));
    }

    @Test
    void updateProduct_WithNonExistingProduct_ThrowsException() {
        String productId = "nonexistent";
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        UpdateProductRequest request = UpdateProductRequest.builder()
                .title("Updated Title")
                .build();

        assertThrows(ProductNotFoundException.class,
                () -> productService.updateProduct(productId, request));
        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(CreateProduct.class));
    }

    @Test
    void updateProduct_WithNullFields_KeepsExistingValues() {
        String productId = "MLB1234567";
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(CreateProduct.class))).thenAnswer(i -> i.getArguments()[0]);

        UpdateProductRequest request = UpdateProductRequest.builder().build();

        CreateProduct result = productService.updateProduct(productId, request);

        assertNotNull(result);
        assertEquals(testProduct.getTitle(), result.getTitle());
        assertEquals(testProduct.getPrice(), result.getPrice());
        assertEquals(testProduct.getDescription(), result.getDescription());
        verify(productRepository).findById(productId);
        verify(productRepository).save(any(CreateProduct.class));
    }

    @Test
    void createProduct_WithValidData_CreatesSuccessfully() {
        CreateProductRequest request = CreateProductRequest.builder()
                .title("New Product")
                .description("New Description")
                .price(new BigDecimal("299.99"))
                .build();

        when(productRepository.save(any(CreateProduct.class))).thenAnswer(i -> {
            CreateProduct savedProduct = (CreateProduct) i.getArguments()[0];
            assertNotNull(savedProduct.getId()); // Verifica que se generó un ID
            assertEquals(request.getTitle(), savedProduct.getTitle());
            assertEquals(request.getDescription(), savedProduct.getDescription());
            assertEquals(request.getPrice(), savedProduct.getPrice());
            assertNotNull(savedProduct.getRating()); // Verifica que se inicializó el rating
            assertEquals(0.0, savedProduct.getRating().getAverageRating());
            assertEquals(0, savedProduct.getRating().getTotalRatings());
            return savedProduct;
        });

        CreateProduct result = productService.createProduct(request);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getPrice(), result.getPrice());
        verify(productRepository).save(any(CreateProduct.class));
    }

    @Test
    void createProduct_WithDefaultValues_AppliesDefaults() {
        CreateProductRequest request = CreateProductRequest.builder()
                .title("New Product")
                .description("New Description")
                .price(new BigDecimal("299.99"))
                .build();

        when(productRepository.save(any(CreateProduct.class))).thenAnswer(i -> i.getArguments()[0]);

        CreateProduct result = productService.createProduct(request);

        assertNotNull(result.getPaymentMethods());
        assertTrue(result.getPaymentMethods().containsAll(Arrays.asList(
            "Tarjeta de crédito",
            "Tarjeta de débito",
            "PayPal",
            "Mercado Pago",
            "Apple Pay"
        )));
        assertNotNull(result.getImages());
        assertEquals(0, result.getImages().size());
        assertEquals(0, result.getAvailableStock());
        verify(productRepository).save(any(CreateProduct.class));
    }
} 