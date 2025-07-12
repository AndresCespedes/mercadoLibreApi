package com.mercadolibre.product_api.service;

import com.mercadolibre.product_api.dto.CreateProductRequest;
import com.mercadolibre.product_api.dto.PagedResponse;
import com.mercadolibre.product_api.dto.ProductSearchParams;
import com.mercadolibre.product_api.dto.UpdateProductRequest;
import com.mercadolibre.product_api.exception.ProductNotFoundException;
import com.mercadolibre.product_api.model.CreateProduct;
import com.mercadolibre.product_api.model.ProductRating;
import com.mercadolibre.product_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private static final List<String> DEFAULT_PAYMENT_METHODS = Arrays.asList(
      "Tarjeta de crédito",
      "Tarjeta de débito",
      "PayPal",
      "Mercado Pago",
      "Apple Pay");

  private final ProductRepository productRepository;

  public CreateProduct getProductById(String id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(
            "Producto no encontrado con ID: " + id));
  }

  public CreateProduct createProduct(CreateProductRequest request) {
    CreateProduct product = new CreateProduct();
    product.setId(UUID.randomUUID().toString());
    product.setTitle(request.getTitle());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setImages(request.getImages());
    product.setSeller(request.getSeller());
    product.setAvailableStock(request.getAvailableStock());
    product.setPaymentMethods(request.getPaymentMethods());
    
    // Inicializar rating con valores por defecto
    ProductRating rating = new ProductRating();
    rating.setAverageRating(0.0);
    rating.setTotalRatings(0);
    rating.setReviews(new ArrayList<>());
    product.setRating(rating);

    applyDefaultValues(product);
    return productRepository.save(product);
  }

  public CreateProduct updateProduct(String id, UpdateProductRequest request) {
    CreateProduct existingProduct = getProductById(id);
    
    // Actualizar solo los campos no nulos
    Optional.ofNullable(request.getTitle()).ifPresent(existingProduct::setTitle);
    Optional.ofNullable(request.getDescription()).ifPresent(existingProduct::setDescription);
    Optional.ofNullable(request.getPrice()).ifPresent(existingProduct::setPrice);
    Optional.ofNullable(request.getImages()).ifPresent(existingProduct::setImages);
    Optional.ofNullable(request.getSeller()).ifPresent(existingProduct::setSeller);
    Optional.ofNullable(request.getAvailableStock()).ifPresent(existingProduct::setAvailableStock);
    Optional.ofNullable(request.getPaymentMethods()).ifPresent(existingProduct::setPaymentMethods);
    Optional.ofNullable(request.getCategory()).ifPresent(existingProduct::setCategory);
    Optional.ofNullable(request.getAttributes()).ifPresent(existingProduct::setAttributes);
    Optional.ofNullable(request.getRating()).ifPresent(existingProduct::setRating);
    
    return productRepository.save(existingProduct);
  }

  private void applyDefaultValues(CreateProduct product) {
    // Agregar métodos de pago por defecto si la lista está vacía
    if (product.getPaymentMethods() == null || product.getPaymentMethods().isEmpty()) {
      product.setPaymentMethods(new ArrayList<>(DEFAULT_PAYMENT_METHODS));
    }

    // Inicializar lista de imágenes si es null
    if (product.getImages() == null) {
      product.setImages(new ArrayList<>());
    }

    if (product.getAvailableStock() == null) {
      product.setAvailableStock(0);
    }
  }

  public void deleteProduct(String id) {
    if (!productRepository.existsById(id)) {
      throw new ProductNotFoundException("Producto no encontrado con ID: " + id);
    }
    productRepository.deleteById(id);
  }

  public PagedResponse<CreateProduct> searchProducts(ProductSearchParams params) {
    List<CreateProduct> allProducts = productRepository.findAll();

    // Aplicar filtros
    List<CreateProduct> filteredProducts = allProducts.stream()
        .filter(buildFilterPredicate(params))
        .sorted(buildSortComparator(params))
        .collect(Collectors.toList());

    // Calcular paginación
    int page = params.getPage() != null ? params.getPage() : 0;
    int size = params.getSize() != null ? params.getSize() : 10;
    int totalElements = filteredProducts.size();
    int totalPages = (int) Math.ceil((double) totalElements / size);

    // Aplicar paginación
    List<CreateProduct> pageContent = filteredProducts.stream()
        .skip((long) page * size)
        .limit(size)
        .collect(Collectors.toList());

    return PagedResponse.<CreateProduct>builder()
        .content(pageContent)
        .pageNumber(page)
        .pageSize(size)
        .totalElements(totalElements)
        .totalPages(totalPages)
        .first(page == 0)
        .last(page == totalPages - 1)
        .build();
  }

  private Predicate<CreateProduct> buildFilterPredicate(ProductSearchParams params) {
    return product -> {
      boolean matchesQuery = params.getQuery() == null ||
          product.getTitle().toLowerCase().contains(params.getQuery().toLowerCase()) ||
          product.getDescription().toLowerCase()
              .contains(params.getQuery().toLowerCase());

      boolean matchesMinPrice = params.getMinPrice() == null ||
          product.getPrice().compareTo(params.getMinPrice()) >= 0;

      boolean matchesMaxPrice = params.getMaxPrice() == null ||
          product.getPrice().compareTo(params.getMaxPrice()) <= 0;

      boolean matchesOfficialStore = params.getIsOfficialStore() == null ||
          product.getSeller().getIsOfficialStore().equals(params.getIsOfficialStore());

      boolean matchesMinRating = params.getMinRating() == null ||
          (product.getRating() != null &&
              product.getRating().getAverageRating() >= params
                  .getMinRating());

      return matchesQuery && matchesMinPrice && matchesMaxPrice &&
          matchesOfficialStore && matchesMinRating;
    };
  }

  private Comparator<CreateProduct> buildSortComparator(ProductSearchParams params) {
    String sortBy = params.getSortBy() != null ? params.getSortBy() : "id";
    boolean ascending = !"desc".equalsIgnoreCase(params.getSortDirection());

    Comparator<CreateProduct> comparator = switch (sortBy.toLowerCase()) {
      case "price" -> Comparator.comparing(CreateProduct::getPrice);
      case "rating" -> Comparator
          .comparing(p -> p.getRating() != null ? p.getRating().getAverageRating() : 0.0);
      default -> Comparator.comparing(CreateProduct::getId);
    };

    return ascending ? comparator : comparator.reversed();
  }
}