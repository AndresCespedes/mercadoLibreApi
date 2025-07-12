package com.mercadolibre.product_api.controller;

import com.mercadolibre.product_api.dto.CreateProductRequest;
import com.mercadolibre.product_api.dto.PagedResponse;
import com.mercadolibre.product_api.dto.ProductSearchParams;
import com.mercadolibre.product_api.dto.UpdateProductRequest;
import com.mercadolibre.product_api.model.CreateProduct;
import com.mercadolibre.product_api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "API para gestionar productos de Mercado Libre")
@CrossOrigin(origins = "*")
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener un producto por ID",
        description = "Retorna un producto específico basado en su ID único"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Producto encontrado exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CreateProduct.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado",
            content = @Content
        )
    })
    public ResponseEntity<CreateProduct> getProductById(
        @Parameter(description = "ID único del producto", required = true)
        @PathVariable String id
    ) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    @PostMapping
    @Operation(
        summary = "Crear un nuevo producto",
        description = "Crea un nuevo producto con la información proporcionada. El rating se inicializa automáticamente."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Producto creado exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CreateProduct.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de producto inválidos",
            content = @Content
        )
    })
    public ResponseEntity<CreateProduct> createProduct(
        @Parameter(description = "Datos del nuevo producto", required = true)
        @Valid @RequestBody CreateProductRequest request
    ) {
        CreateProduct createdProduct = productService.createProduct(request);
        return ResponseEntity.created(URI.create("/api/products/" + createdProduct.getId()))
                .body(createdProduct);
    }
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar un producto existente",
        description = "Actualiza los campos especificados de un producto existente. Todos los campos son opcionales."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Producto actualizado exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CreateProduct.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de producto inválidos",
            content = @Content
        )
    })
    public ResponseEntity<CreateProduct> updateProduct(
        @Parameter(description = "ID del producto a actualizar", required = true)
        @PathVariable String id,
        @Parameter(description = "Datos actualizados del producto", required = true)
        @Valid @RequestBody UpdateProductRequest request
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar un producto",
        description = "Elimina permanentemente un producto del sistema"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Producto eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado",
            content = @Content
        )
    })
    public ResponseEntity<Void> deleteProduct(
        @Parameter(description = "ID del producto a eliminar", required = true)
        @PathVariable String id
    ) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    @Operation(
        summary = "Listar todos los productos",
        description = "Retorna una lista paginada de todos los productos"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Productos encontrados exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PagedResponse.class)
            )
        )
    })
    public ResponseEntity<PagedResponse<CreateProduct>> getAllProducts(
        @Parameter(description = "Número de página (desde 0)")
        @RequestParam(defaultValue = "0") Integer page,
        
        @Parameter(description = "Tamaño de página (cantidad de elementos)")
        @RequestParam(defaultValue = "10") Integer size,
        
        @Parameter(description = "Campo para ordenar (id, price, rating)")
        @RequestParam(defaultValue = "id") String sortBy,
        
        @Parameter(description = "Dirección del ordenamiento (asc, desc)")
        @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        ProductSearchParams searchParams = ProductSearchParams.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        
        return ResponseEntity.ok(productService.searchProducts(searchParams));
    }
    
    @GetMapping("/search")
    @Operation(
        summary = "Buscar productos con filtros y paginación",
        description = "Permite buscar productos aplicando diversos filtros, ordenamiento y paginación"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Búsqueda realizada exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PagedResponse.class)
            )
        )
    })
    public ResponseEntity<PagedResponse<CreateProduct>> searchProducts(
        @Parameter(description = "Término de búsqueda en título y descripción")
        @RequestParam(required = false) String query,
        
        @Parameter(description = "Precio mínimo del producto")
        @RequestParam(required = false) BigDecimal minPrice,
        
        @Parameter(description = "Precio máximo del producto")
        @RequestParam(required = false) BigDecimal maxPrice,
        
        @Parameter(description = "Filtrar por tienda oficial (true/false)")
        @RequestParam(required = false) Boolean isOfficialStore,
        
        @Parameter(description = "Calificación mínima del producto")
        @RequestParam(required = false) Double minRating,
        
        @Parameter(description = "Número de página (desde 0)")
        @RequestParam(defaultValue = "0") Integer page,
        
        @Parameter(description = "Tamaño de página (cantidad de elementos)")
        @RequestParam(defaultValue = "10") Integer size,
        
        @Parameter(description = "Campo para ordenar (id, price, rating)")
        @RequestParam(defaultValue = "id") String sortBy,
        
        @Parameter(description = "Dirección del ordenamiento (asc, desc)")
        @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        ProductSearchParams searchParams = ProductSearchParams.builder()
                .query(query)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .isOfficialStore(isOfficialStore)
                .minRating(minRating)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        
        return ResponseEntity.ok(productService.searchProducts(searchParams));
    }
} 