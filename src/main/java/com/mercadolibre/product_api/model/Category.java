package com.mercadolibre.product_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Categoría de productos")
public class Category {
    @Schema(description = "ID único de la categoría", example = "TECH")
    @NotBlank(message = "El ID de la categoría es obligatorio")
    private String id;

    @Schema(description = "Nombre de la categoría", example = "Tecnología")
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String name;

    @Schema(description = "Descripción de la categoría", example = "Productos electrónicos y tecnológicos")
    private String description;

    @Schema(description = "ID de la categoría padre (si es una subcategoría)", example = "ELECTRONICS")
    private String parentId;

    @Schema(description = "Lista de características específicas de la categoría", example = "[\"Marca\", \"Modelo\", \"Año\"]")
    private List<String> attributes = new ArrayList<>();

    @Schema(description = "Indica si la categoría está activa", example = "true")
    private Boolean active = true;
} 