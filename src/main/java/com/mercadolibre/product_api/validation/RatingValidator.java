package com.mercadolibre.product_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RatingValidatorImpl.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RatingValidator {
    String message() default "La calificación debe estar entre 1 y 5";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 