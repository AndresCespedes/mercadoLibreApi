package com.mercadolibre.product_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.mercadolibre.product_api.model.ProductRating;

public class RatingValidatorImpl implements ConstraintValidator<RatingValidator, ProductRating> {

    @Override
    public void initialize(RatingValidator constraintAnnotation) {
    }

    @Override
    public boolean isValid(ProductRating value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // La validación de requerido se hace con @NotNull
        }
        // Validar el rango de averageRating
        double rating = value.getAverageRating();
        if (rating < 0 || rating > 5) {
            return false;
        }
        // Validar cada review si existe
        if (value.getReviews() != null) {
            for (var review : value.getReviews()) {
                if (review.getRating() < 0 || review.getRating() > 5) {
                    return false;
                }
            }
        }
        return true;
    }
} 