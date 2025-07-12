package com.mercadolibre.product_api.validation;

import com.mercadolibre.product_api.model.ProductRating;
import com.mercadolibre.product_api.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class RatingValidatorTest {

    private RatingValidatorImpl validatorImpl;

    @BeforeEach
    void setUp() {
        validatorImpl = new RatingValidatorImpl();
    }

    @Test
    void validate_ValidRating_ReturnsTrue() {
        ProductRating rating = new ProductRating();
        rating.setAverageRating(4.5);
        rating.setTotalRatings(10);
        rating.setReviews(Arrays.asList(
            createValidReview(4, "Good product"),
            createValidReview(5, "Excellent")
        ));

        assertTrue(validatorImpl.isValid(rating, null));
    }

    @Test
    void validate_InvalidAverageRating_ReturnsFalse() {
        ProductRating rating = new ProductRating();
        rating.setAverageRating(6.0); // Mayor que 5
        rating.setTotalRatings(10);
        rating.setReviews(Collections.singletonList(
            createValidReview(4, "Good product")
        ));

        assertFalse(validatorImpl.isValid(rating, null));
    }

    @Test
    void validate_NegativeAverageRating_ReturnsFalse() {
        ProductRating rating = new ProductRating();
        rating.setAverageRating(-1.0);
        rating.setTotalRatings(10);
        rating.setReviews(Collections.singletonList(
            createValidReview(4, "Good product")
        ));

        assertFalse(validatorImpl.isValid(rating, null));
    }

    @Test
    void validate_InvalidReviewRating_ReturnsFalse() {
        ProductRating rating = new ProductRating();
        rating.setAverageRating(4.0);
        rating.setTotalRatings(1);
        rating.setReviews(Collections.singletonList(
            createValidReview(6, "Invalid rating") // Rating mayor que 5
        ));

        assertFalse(validatorImpl.isValid(rating, null));
    }

    @Test
    void validate_NullRating_ReturnsTrue() {
        assertTrue(validatorImpl.isValid(null, null));
    }

    private Review createValidReview(int rating, String comment) {
        Review review = new Review();
        review.setUserId("user123");
        review.setRating(rating);
        review.setComment(comment);
        review.setDate(LocalDate.now().toString());
        return review;
    }
} 