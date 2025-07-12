package com.mercadolibre.product_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reseña de un producto")
public class Review {
    @Schema(description = "ID del usuario que realizó la reseña", example = "user123")
    private String userId;

    @Schema(description = "Comentario de la reseña", example = "Excelente producto, muy satisfecho con la compra")
    private String comment;

    @Schema(description = "Calificación numérica (1-5)", example = "5")
    private Integer rating;

    @Schema(description = "Fecha de la reseña", example = "2024-03-15T14:30:00Z")
    private String date;
} 