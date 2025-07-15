package com.mercadolibre.product_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO que encapsula los parámetros de búsqueda para productos.
 */
@Data
@Builder
@Schema(description = "Parámetros de búsqueda para productos")
public class ProductSearchParams {
    
    @Schema(description = "Término de búsqueda en título y descripción")
    private String query;
    
    @Schema(description = "Precio mínimo del producto")
    private BigDecimal minPrice;
    
    @Schema(description = "Precio máximo del producto")
    private BigDecimal maxPrice;
    
    @Schema(description = "Filtrar por tienda oficial")
    private Boolean isOfficialStore;
    
    @Schema(description = "Calificación mínima del producto")
    private Double minRating;

    @Schema(description = "Nombre de la tienda para filtrar")
    private String storeName;
} 