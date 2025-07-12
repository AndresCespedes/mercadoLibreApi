package com.mercadolibre.product_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.validator.routines.UrlValidator;
import java.util.List;

public class URLValidatorImpl implements ConstraintValidator<URLValidator, Object> {
    private final UrlValidator urlValidator;

    public URLValidatorImpl() {
        this.urlValidator = new UrlValidator(new String[]{"http", "https"});
    }

    @Override
    public void initialize(URLValidator constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value instanceof String) {
            return urlValidator.isValid((String) value);
        }

        if (value instanceof List<?>) {
            List<?> list = (List<?>) value;
            if (list.isEmpty()) {
                return true;
            }

            for (Object item : list) {
                if (!(item instanceof String) || !urlValidator.isValid((String) item)) {
                    if (context != null) {
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate("La URL '" + item + "' no es válida")
                                .addConstraintViolation();
                    }
                    return false;
                }
            }
            return true;
        }

        return false;
    }
} 