package com.mercadolibre.product_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Información de calificaciones y reseñas de un producto")
public class ProductRating {
    @Schema(description = "Promedio de calificaciones", example = "4.5")
    private Double averageRating;

    @Schema(description = "Número total de calificaciones recibidas", example = "100")
    private Integer totalRatings;

    @Schema(description = "Lista de reseñas del producto")
    private List<Review> reviews = new ArrayList<>();
} 