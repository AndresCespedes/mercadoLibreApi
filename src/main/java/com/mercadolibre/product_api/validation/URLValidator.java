package com.mercadolibre.product_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = URLValidatorImpl.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface URLValidator {
    String message() default "La URL proporcionada no es válida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 