package com.mercadolibre.product_api.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class URLValidatorTest {

    private URLValidatorImpl validatorImpl;

    @BeforeEach
    void setUp() {
        validatorImpl = new URLValidatorImpl();
    }

    @Test
    void validate_ValidURLs_ReturnsTrue() {
        List<String> urls = Arrays.asList(
            "https://example.com/image1.jpg",
            "https://example.com/image2.png",
            "http://secure-site.com/photo.jpeg"
        );

        assertTrue(validatorImpl.isValid(urls, null));
    }

    @Test
    void validate_InvalidURL_ReturnsFalse() {
        List<String> urls = Collections.singletonList("not-a-url");

        assertFalse(validatorImpl.isValid(urls, null));
    }

    @Test
    void validate_MixedValidAndInvalidURLs_ReturnsFalse() {
        List<String> urls = Arrays.asList(
            "https://example.com/valid.jpg",
            "invalid-url",
            "https://another.com/image.png"
        );

        assertFalse(validatorImpl.isValid(urls, null));
    }

    @Test
    void validate_EmptyList_ReturnsTrue() {
        assertTrue(validatorImpl.isValid(Collections.emptyList(), null));
    }

    @Test
    void validate_NullList_ReturnsTrue() {
        assertTrue(validatorImpl.isValid(null, null));
    }

    @Test
    void validate_URLWithoutProtocol_ReturnsFalse() {
        List<String> urls = Collections.singletonList("www.example.com/image.jpg");

        assertFalse(validatorImpl.isValid(urls, null));
    }

    @Test
    void validate_URLWithInvalidProtocol_ReturnsFalse() {
        List<String> urls = Collections.singletonList("ftp://example.com/image.jpg");

        assertFalse(validatorImpl.isValid(urls, null));
    }

    @Test
    void validate_URLWithQueryParams_ReturnsTrue() {
        List<String> urls = Collections.singletonList(
            "https://example.com/image.jpg?size=large&format=webp"
        );

        assertTrue(validatorImpl.isValid(urls, null));
    }
} 