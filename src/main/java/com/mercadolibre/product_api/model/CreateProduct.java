package com.mercadolibre.product_api.model;

import com.mercadolibre.product_api.validation.RatingValidator;
import com.mercadolibre.product_api.validation.URLValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Representa un producto completo con todos sus detalles")
public class CreateProduct {
    @Schema(description = "ID único del producto", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;

    @Schema(description = "Título del producto", example = "iPhone 13 Pro Max")
    @NotBlank(message = "El título del producto es obligatorio")
    private String title;

    @Schema(description = "Descripción detallada del producto", example = "Smartphone Apple iPhone 13 Pro Max 256GB")
    @NotBlank(message = "La descripción del producto es obligatoria")
    private String description;

    @Schema(description = "Precio del producto", example = "999.99")
    @NotNull(message = "El precio del producto es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private BigDecimal price;

    @Schema(description = "Lista de URLs de imágenes del producto")
    @URLValidator
    private List<String> images = new ArrayList<>();

    @Schema(description = "Información del vendedor")
    @NotNull(message = "La información del vendedor es obligatoria")
    private Seller seller;

    @Schema(description = "Cantidad de unidades disponibles", example = "100")
    private Integer availableStock;

    @Schema(description = "Métodos de pago aceptados")
    private List<String> paymentMethods = new ArrayList<>();

    @Schema(description = "Información de calificaciones y reseñas del producto")
    private ProductRating rating;

    @Schema(description = "Categoría del producto")
    @NotNull(message = "La categoría del producto es obligatoria")
    private Category category;

    @Schema(description = "Atributos específicos de la categoría", example = "{\"Marca\": \"Apple\", \"Modelo\": \"iPhone 13\"}")
    private Map<String, String> attributes;
}