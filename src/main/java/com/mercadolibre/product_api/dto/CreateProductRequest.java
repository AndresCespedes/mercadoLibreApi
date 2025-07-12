package com.mercadolibre.product_api.dto;

import com.mercadolibre.product_api.model.Category;
import com.mercadolibre.product_api.model.Seller;
import com.mercadolibre.product_api.validation.URLValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear un nuevo producto")
public class CreateProductRequest {
    
    @NotBlank(message = "El título del producto no puede estar vacío")
    @Schema(description = "Título del producto", example = "iPhone 13 Pro Max")
    private String title;
    
    @NotBlank(message = "La descripción del producto no puede estar vacía")
    @Schema(description = "Descripción detallada del producto", example = "Smartphone Apple iPhone 13 Pro Max 256GB")
    private String description;
    
    @NotNull(message = "El precio del producto es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    @Schema(description = "Precio del producto", example = "999.99")
    private BigDecimal price;
    
    @NotEmpty(message = "Debe proporcionar al menos una imagen del producto")
    @URLValidator(message = "Una o más URLs de imágenes no son válidas")
    @Schema(description = "Lista de URLs de imágenes del producto")
    private List<String> images = new ArrayList<>();
    
    @NotNull(message = "La información del vendedor es obligatoria")
    @Schema(description = "Información del vendedor")
    private Seller seller;
    
    @Schema(description = "Cantidad de unidades disponibles", example = "100")
    private Integer availableStock;
    
    @Schema(description = "Métodos de pago aceptados")
    private List<String> paymentMethods = new ArrayList<>();
    
    @NotNull(message = "La categoría del producto es obligatoria")
    @Schema(description = "Categoría del producto")
    private Category category;
    
    @Schema(description = "Atributos específicos de la categoría", example = "{\"Marca\": \"Apple\", \"Modelo\": \"iPhone 13\"}")
    private Map<String, String> attributes;
} 