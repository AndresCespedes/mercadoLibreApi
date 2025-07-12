package com.mercadolibre.product_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del vendedor del producto")
public class Seller {
    @Schema(description = "ID único del vendedor", example = "SELLER123")
    @NotBlank(message = "El ID del vendedor es obligatorio")
    private String id;

    @Schema(description = "Nombre del vendedor", example = "Tienda Oficial Apple")
    @NotBlank(message = "El nombre del vendedor es obligatorio")
    private String name;

    @Schema(description = "Indica si es una tienda oficial", example = "true")
    @NotNull(message = "Debe especificar si es tienda oficial")
    private Boolean isOfficialStore;

    @Schema(description = "Calificación del vendedor (1-5)", example = "4.8")
    private Double rating;
} 