package com.mercadolibre.product_api.controller;

import com.mercadolibre.product_api.dto.PagedResponse;
import com.mercadolibre.product_api.dto.ProductSearchParams;
import com.mercadolibre.product_api.dto.UpdateProductRequest;
import com.mercadolibre.product_api.exception.ProductNotFoundException;
import com.mercadolibre.product_api.model.CreateProduct;
import com.mercadolibre.product_api.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getProductById_ExistingProduct_ReturnsProduct() throws Exception {
        CreateProduct product = CreateProduct.builder()
                .id("MLB1234567")
                .title("Test Product")
                .description("Test Description")
                .build();

        when(productService.getProductById("MLB1234567")).thenReturn(product);

        mockMvc.perform(get("/api/products/MLB1234567"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("MLB1234567")))
                .andExpect(jsonPath("$.title", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")));

        verify(productService).getProductById("MLB1234567");
    }

    @Test
    void getProductById_NonExistingProduct_ReturnsNotFound() throws Exception {
        when(productService.getProductById("nonexistent"))
                .thenThrow(new ProductNotFoundException("Producto no encontrado con ID: nonexistent"));

        mockMvc.perform(get("/api/products/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Producto no encontrado")));

        verify(productService).getProductById("nonexistent");
    }

    @Test
    void searchProducts_WithDefaultParams_ReturnsAllProducts() throws Exception {
        List<CreateProduct> products = Arrays.asList(
                CreateProduct.builder().id("MLB1234567").title("Product 1").build(),
                CreateProduct.builder().id("MLB7654321").title("Product 2").build()
        );

        Page<CreateProduct> pagedResponse = new PageImpl<>(products);

        when(productService.searchProducts(any(ProductSearchParams.class), any(Pageable.class)))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is("MLB1234567")))
                .andExpect(jsonPath("$.content[1].id", is("MLB7654321")))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(true)));

        verify(productService).searchProducts(any(ProductSearchParams.class), any(Pageable.class));
    }

    @Test
    void searchProducts_WithFilters_ReturnsFilteredProducts() throws Exception {
        List<CreateProduct> filteredProducts = Arrays.asList(
            CreateProduct.builder()
                .id("MLB1234567")
                .title("iPhone")
                .price(new BigDecimal("999.99"))
                .build()
        );

        Page<CreateProduct> response = new PageImpl<>(filteredProducts);

        when(productService.searchProducts(any(ProductSearchParams.class), any(Pageable.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/products/search")
                .param("query", "iPhone")
                .param("minPrice", "500")
                .param("maxPrice", "1000")
                .param("isOfficialStore", "true")
                .param("minRating", "4.0")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "price,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].title", is("iPhone")))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(productService).searchProducts(any(ProductSearchParams.class), any(Pageable.class));
    }

    @Test
    void getAllProducts_WithPagination_ReturnsPagedProducts() throws Exception {
        List<CreateProduct> products = Arrays.asList(
            CreateProduct.builder()
                .id("MLB1234567")
                .title("Product 1")
                .build(),
            CreateProduct.builder()
                .id("MLB7654321")
                .title("Product 2")
                .build()
        );

        Page<CreateProduct> response = new PageImpl<>(products);

        when(productService.searchProducts(any(ProductSearchParams.class), any(Pageable.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "2")
                .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is("Product 1")))
                .andExpect(jsonPath("$.content[1].title", is("Product 2")));

        verify(productService).searchProducts(any(ProductSearchParams.class), any(Pageable.class));
    }

    @Test
    void updateProduct_WithValidData_ReturnsUpdatedProduct() throws Exception {
        CreateProduct updatedProduct = CreateProduct.builder()
                .id("MLB1234567")
                .title("Updated Product")
                .description("Updated Description")
                .price(new BigDecimal("150.00"))
                .build();

        when(productService.updateProduct(eq("MLB1234567"), any(UpdateProductRequest.class)))
                .thenReturn(updatedProduct);

        String requestBody = """
                {
                    "title": "Updated Product",
                    "description": "Updated Description",
                    "price": 150.00
                }""";

        mockMvc.perform(put("/api/products/MLB1234567")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("MLB1234567")))
                .andExpect(jsonPath("$.title", is("Updated Product")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.price", is(150.00)));

        verify(productService).updateProduct(eq("MLB1234567"), any(UpdateProductRequest.class));
    }

    @Test
    void updateProduct_WithNonExistingProduct_ReturnsNotFound() throws Exception {
        when(productService.updateProduct(eq("nonexistent"), any(UpdateProductRequest.class)))
                .thenThrow(new ProductNotFoundException("Producto no encontrado con ID: nonexistent"));

        String requestBody = """
                {
                    "title": "Updated Product"
                }""";

        mockMvc.perform(put("/api/products/nonexistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Producto no encontrado")));

        verify(productService).updateProduct(eq("nonexistent"), any(UpdateProductRequest.class));
    }

    @Test
    void updateProduct_WithInvalidData_ReturnsBadRequest() throws Exception {
        String requestBody = """
                {
                    "price": -100.00
                }""";

        mockMvc.perform(put("/api/products/MLB1234567")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Error de validación")))
                .andExpect(jsonPath("$.details.price", containsString("mayor que cero")));

        verify(productService, never()).updateProduct(any(), any());
    }

    @Test
    void deleteProduct_ExistingProduct_ReturnsNoContent() throws Exception {
        doNothing().when(productService).deleteProduct("MLB1234567");

        mockMvc.perform(delete("/api/products/MLB1234567"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct("MLB1234567");
    }

    @Test
    void deleteProduct_NonExistingProduct_ReturnsNotFound() throws Exception {
        doThrow(new ProductNotFoundException("Producto no encontrado con ID: nonexistent"))
                .when(productService).deleteProduct("nonexistent");

        mockMvc.perform(delete("/api/products/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Producto no encontrado")));

        verify(productService).deleteProduct("nonexistent");
    }
} 