// Importamos las clases necesarias para el controlador
package com.mercadolibre.product_api.controller;

// Importamos los DTOs (Data Transfer Objects)
import com.mercadolibre.product_api.dto.CreateProductRequest;
import com.mercadolibre.product_api.dto.PagedResponse;
import com.mercadolibre.product_api.dto.ProductSearchParams;
import com.mercadolibre.product_api.dto.UpdateProductRequest;
// Importamos el modelo de producto
import com.mercadolibre.product_api.model.CreateProduct;
// Importamos el servicio que maneja la lógica de negocio
import com.mercadolibre.product_api.service.ProductService;
// Importamos anotaciones de OpenAPI/Swagger para documentación
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
// Importamos anotaciones de validación
import jakarta.validation.Valid;
// Importamos Lombok para reducir código boilerplate
import lombok.RequiredArgsConstructor;
// Importamos clases de Spring para manejo de HTTP y paginación
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

// Importamos clases de utilidad
import java.math.BigDecimal;
import java.net.URI;

/**
 * Controlador REST que maneja las operaciones CRUD y búsqueda de productos.
 * Implementa la capa de presentación de la API.
 */
@RestController // Indica que es un controlador REST
@RequestMapping("/api/products") // Define la ruta base para todos los endpoints
@RequiredArgsConstructor // Genera constructor con campos final (inyección de dependencias)
@Tag(name = "Productos", description = "API para gestionar productos de Mercado Libre") // Añade una etiqueta a la API (swagger)
@CrossOrigin(origins = "*") // Permite peticiones CORS desde cualquier origen
public class ProductController {
    
    // Inyectamos el servicio de productos
    private final ProductService productService;
    
    /**
     * Obtiene un producto por su ID.
     * 
     * @param id ID único del producto a buscar
     * @return ResponseEntity con el producto si existe
     * @throws ProductNotFoundException si el producto no existe
     */
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
                mediaType = MediaType.APPLICATION_JSON_VALUE, // Tipo de media (JSON)
                schema = @Schema(implementation = CreateProduct.class) // Esquema de la respuesta (CreateProduct)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado",
            content = @Content
        )
    })
    public ResponseEntity<CreateProduct> getProductById(
        @Parameter(description = "ID único del producto", required = true) // Documenta el parámetro y lo marca como requerido
        @PathVariable String id // Captura el ID de la URL
    ) {
        // Delegamos la búsqueda al servicio y retornamos respuesta HTTP 200 si existe
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    /**
     * Crea un nuevo producto.
     * 
     * @param request DTO con los datos del nuevo producto
     * @return ResponseEntity con el producto creado
     */
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
        @Valid @RequestBody CreateProductRequest request // Valida y mapea el cuerpo de la petición
    ) {
        // Creamos el producto a través del servicio
        CreateProduct createdProduct = productService.createProduct(request);
        // Retornamos respuesta HTTP 201 con la ubicación del nuevo recurso
        return ResponseEntity.created(URI.create("/api/products/" + createdProduct.getId())) // Retorna la ubicación del nuevo recurso
                .body(createdProduct);
    }
    
    /**
     * Actualiza parcialmente un producto existente.
     * 
     * @param id ID del producto a actualizar
     * @param request DTO con los datos a actualizar
     * @return ResponseEntity con el producto actualizado
     * @throws ProductNotFoundException si el producto no existe
     */
    @PatchMapping("/{id}")
    @Operation(
        summary = "Actualizar parcialmente un producto existente",
        description = "Actualiza solo los campos especificados de un producto existente. Todos los campos son opcionales."
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
    
    /**
     * Elimina un producto existente.
     * 
     * @param id ID del producto a eliminar
     * @return ResponseEntity sin contenido
     * @throws ProductNotFoundException si el producto no existe
     */
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
        @PathVariable String id // Captura el ID de la URL
    ) {
        // Eliminamos el producto a través del servicio
        productService.deleteProduct(id);
        // Retornamos respuesta HTTP 204 (Sin contenido)
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Lista todos los productos con paginación y ordenamiento.
     * 
     * @param page Número de página (desde 0)
     * @param size Tamaño de página
     * @param sortBy Campo para ordenar
     * @param sortDirection Dirección del ordenamiento
     * @return ResponseEntity con la lista paginada de productos
     */
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
                schema = @Schema(implementation = Page.class)
            )
        )
    })
    public ResponseEntity<Page<CreateProduct>> getAllProducts(
        @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }
    
    /**
     * Busca productos aplicando filtros, paginación y ordenamiento.
     * 
     * @param query Término de búsqueda
     * @param minPrice Precio mínimo
     * @param maxPrice Precio máximo
     * @param isOfficialStore Filtro de tienda oficial
     * @param minRating Calificación mínima
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortBy Campo para ordenar
     * @param sortDirection Dirección del ordenamiento
     * @return ResponseEntity con la lista filtrada y paginada de productos
     */
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
                schema = @Schema(implementation = Page.class)
            )
        )
    })
    public ResponseEntity<Page<CreateProduct>> searchProducts(
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

        @Parameter(description = "Nombre de la tienda para filtrar")
        @RequestParam(required = false) String storeName,
        
        @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        ProductSearchParams searchParams = ProductSearchParams.builder()
                .query(query)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .isOfficialStore(isOfficialStore)
                .minRating(minRating)
                .storeName(storeName)
                .build();
        
        return ResponseEntity.ok(productService.searchProducts(searchParams, pageable));
    }
} 