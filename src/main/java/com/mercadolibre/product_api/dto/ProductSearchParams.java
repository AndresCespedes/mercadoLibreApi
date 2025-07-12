package com.mercadolibre.product_api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductSearchParams {
    private String query;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean isOfficialStore;
    private Double minRating;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;

    public static ProductSearchParams getDefaultParams() {
        return ProductSearchParams.builder()
                .page(0)
                .size(10)
                .sortBy("id")
                .sortDirection("asc")
                .build();
    }
} 