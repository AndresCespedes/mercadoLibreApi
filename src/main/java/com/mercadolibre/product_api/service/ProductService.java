// Importamos las clases necesarias para el servicio
package com.mercadolibre.product_api.service;

// Importamos los DTOs
import com.mercadolibre.product_api.dto.CreateProductRequest;
import com.mercadolibre.product_api.dto.PagedResponse;
import com.mercadolibre.product_api.dto.ProductSearchParams;
import com.mercadolibre.product_api.dto.UpdateProductRequest;
// Importamos la excepción personalizada
import com.mercadolibre.product_api.exception.ProductNotFoundException;
// Importamos las clases del modelo
import com.mercadolibre.product_api.model.CreateProduct;
import com.mercadolibre.product_api.model.ProductRating;
// Importamos el repositorio
import com.mercadolibre.product_api.repository.ProductRepository;
// Importamos Lombok para reducir código boilerplate
import lombok.RequiredArgsConstructor;
// Importamos anotación de servicio de Spring
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

// Importamos clases de utilidad
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Servicio que implementa la lógica de negocio para la gestión de productos.
 * Actúa como intermediario entre el controlador y el repositorio.
 */
@Service // Marca la clase como un servicio de Spring
@RequiredArgsConstructor // Genera constructor con campos final
public class ProductService {

    // Lista de métodos de pago predeterminados
    private static final List<String> DEFAULT_PAYMENT_METHODS = Arrays.asList(
            "Tarjeta de crédito",
            "Tarjeta de débito",
            "PayPal",
            "Mercado Pago",
            "Apple Pay");

    // Inyectamos el repositorio de productos
    private final ProductRepository productRepository;

    /**
     * Obtiene un producto por su ID.
     * 
     * @param id ID del producto a buscar
     * @return Producto encontrado
     * @throws ProductNotFoundException si el producto no existe
     */
    public CreateProduct getProductById(String id) {
        // Buscamos el producto y lanzamos excepción si no existe
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Producto no encontrado con ID: " + id));
    }

    /**
     * Crea un nuevo producto.
     * 
     * @param request DTO con los datos del nuevo producto
     * @return Producto creado
     */
    public CreateProduct createProduct(CreateProductRequest request) {
        // Creamos una nueva instancia de producto
        CreateProduct product = new CreateProduct();
        // Generamos un ID único
        product.setId(UUID.randomUUID().toString());
        // Copiamos los datos del request al producto
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImages(request.getImages());
        product.setSeller(request.getSeller());
        product.setAvailableStock(request.getAvailableStock());
        product.setPaymentMethods(request.getPaymentMethods());

        // Inicializamos el rating con valores por defecto
        ProductRating rating = new ProductRating();
        rating.setAverageRating(0.0);
        rating.setTotalRatings(0);
        rating.setReviews(new ArrayList<>());
        product.setRating(rating);

        // Aplicamos valores por defecto para campos opcionales
        applyDefaultValues(product);
        // Guardamos y retornamos el producto
        return productRepository.save(product);
    }

    /**
     * Actualiza un producto existente.
     * 
     * @param id      ID del producto a actualizar
     * @param request DTO con los datos a actualizar
     * @return Producto actualizado
     * @throws ProductNotFoundException si el producto no existe
     */
    public CreateProduct updateProduct(String id, UpdateProductRequest request) {
        // Obtenemos el producto existente
        CreateProduct existingProduct = getProductById(id);

        // Actualizamos solo los campos no nulos usando Optional
        Optional.ofNullable(request.getTitle()).ifPresent(existingProduct::setTitle); // Si el título no es nulo,
                                                                                      // actualizamos el producto con el
                                                                                      // título del request
        Optional.ofNullable(request.getDescription()).ifPresent(existingProduct::setDescription);
        Optional.ofNullable(request.getPrice()).ifPresent(existingProduct::setPrice);
        Optional.ofNullable(request.getImages()).ifPresent(existingProduct::setImages); // si llega una lista de
                                                                                        // imagenes, actualizamos el
                                                                                        // producto con las imagenes del
                                                                                        // request caso contrario se
                                                                                        // mantiene la lista existente
        Optional.ofNullable(request.getSeller()).ifPresent(existingProduct::setSeller);
        Optional.ofNullable(request.getAvailableStock()).ifPresent(existingProduct::setAvailableStock);
        Optional.ofNullable(request.getPaymentMethods()).ifPresent(existingProduct::setPaymentMethods);
        Optional.ofNullable(request.getCategory()).ifPresent(existingProduct::setCategory);
        Optional.ofNullable(request.getAttributes()).ifPresent(existingProduct::setAttributes);
        Optional.ofNullable(request.getRating()).ifPresent(existingProduct::setRating);

        // Guardamos y retornamos el producto actualizado
        return productRepository.save(existingProduct);
    }

    /**
     * Aplica valores por defecto a campos opcionales del producto.
     * 
     * @param product Producto a inicializar
     */
    private void applyDefaultValues(CreateProduct product) {
        // Agregamos métodos de pago por defecto si la lista está vacía
        if (product.getPaymentMethods() == null || product.getPaymentMethods().isEmpty()) {
            product.setPaymentMethods(new ArrayList<>(DEFAULT_PAYMENT_METHODS));
        }

        // Inicializamos lista de imágenes si es null
        if (product.getImages() == null) {
            product.setImages(new ArrayList<>());
        }

        // Establecemos stock inicial en 0 si es null
        if (product.getAvailableStock() == null) {
            product.setAvailableStock(0);
        }
    }

    /**
     * Elimina un producto por su ID.
     * 
     * @param id ID del producto a eliminar
     * @throws ProductNotFoundException si el producto no existe
     */
    public void deleteProduct(String id) {
        // Verificamos que el producto exista
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Producto no encontrado con ID: " + id);
        }
        // Eliminamos el producto
        productRepository.deleteById(id);
    }

    /**
     * Obtiene todos los productos con paginación.
     * 
     * @param pageable Información de paginación y ordenamiento
     * @return Página de productos
     */
    public Page<CreateProduct> getAllProducts(Pageable pageable) {
        List<CreateProduct> allProducts = productRepository.findAll();
        
        // Ordenamos según el Pageable
        List<CreateProduct> sortedProducts = allProducts.stream()
            .sorted((p1, p2) -> {
                for (var order : pageable.getSort()) {
                    int comparison = compareByField(p1, p2, order.getProperty());
                    if (comparison != 0) {
                        return order.isAscending() ? comparison : -comparison;
                    }
                }
                return 0;
            })
            .collect(Collectors.toList());

        // Aplicamos la paginación
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedProducts.size());
        
        List<CreateProduct> pageContent = sortedProducts.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, sortedProducts.size());
    }

    /**
     * Busca productos aplicando filtros y paginación.
     * 
     * @param params Parámetros de búsqueda
     * @param pageable Información de paginación y ordenamiento
     * @return Página de productos filtrados
     */
    public Page<CreateProduct> searchProducts(ProductSearchParams params, Pageable pageable) {
        List<CreateProduct> allProducts = productRepository.findAll();

        // Aplicamos filtros
        List<CreateProduct> filteredProducts = allProducts.stream()
                .filter(buildFilterPredicate(params))
                .collect(Collectors.toList());

        // Ordenamos según el Pageable
        List<CreateProduct> sortedProducts = filteredProducts.stream()
            .sorted((p1, p2) -> {
                for (var order : pageable.getSort()) {
                    int comparison = compareByField(p1, p2, order.getProperty());
                    if (comparison != 0) {
                        return order.isAscending() ? comparison : -comparison;
                    }
                }
                return 0;
            })
            .collect(Collectors.toList());

        // Aplicamos la paginación
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedProducts.size());
        
        List<CreateProduct> pageContent = sortedProducts.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, sortedProducts.size());
    }

    /**
     * Construye el predicado para filtrar productos.
     * 
     * @param params Parámetros de búsqueda
     * @return Predicado que combina todos los filtros
     */
    private Predicate<CreateProduct> buildFilterPredicate(ProductSearchParams params) {
        return product -> {
            // Filtramos por término de búsqueda en título y descripción
            boolean matchesQuery = params.getQuery() == null ||
                    product.getTitle().toLowerCase().contains(params.getQuery().toLowerCase()) ||
                    (product.getDescription() != null && 
                    product.getDescription().toLowerCase().contains(params.getQuery().toLowerCase()));

            // Filtramos por precio mínimo
            boolean matchesMinPrice = params.getMinPrice() == null ||
                    product.getPrice().compareTo(params.getMinPrice()) >= 0;

            // Filtramos por precio máximo
            boolean matchesMaxPrice = params.getMaxPrice() == null ||
                    product.getPrice().compareTo(params.getMaxPrice()) <= 0;

            // Filtramos por tienda oficial
            boolean matchesOfficialStore = params.getIsOfficialStore() == null ||
                    (product.getSeller() != null && 
                    product.getSeller().getIsOfficialStore() == params.getIsOfficialStore());

            // Filtramos por calificación mínima
            boolean matchesMinRating = params.getMinRating() == null ||
                    (product.getRating() != null && 
                    product.getRating().getAverageRating() >= params.getMinRating());

            // Filtramos por nombre de tienda
            boolean matchesStoreName = params.getStoreName() == null ||
                    (product.getSeller() != null && 
                    product.getSeller().getStoreName() != null &&
                    product.getSeller().getStoreName().toLowerCase()
                            .contains(params.getStoreName().toLowerCase()));

            // Combinamos todos los filtros
            return matchesQuery && matchesMinPrice && matchesMaxPrice &&
                    matchesOfficialStore && matchesMinRating && matchesStoreName;
        };
    }

    /**
     * Compara dos productos por un campo específico.
     * 
     * @param p1 Primer producto
     * @param p2 Segundo producto
     * @param field Campo a comparar
     * @return Resultado de la comparación
     */
    private int compareByField(CreateProduct p1, CreateProduct p2, String field) {
        return switch (field) {
            case "id" -> p1.getId().compareTo(p2.getId());
            case "price" -> p1.getPrice().compareTo(p2.getPrice());
            case "rating" -> Double.compare(
                p1.getRating().getAverageRating(),
                p2.getRating().getAverageRating()
            );
            case "title" -> p1.getTitle().compareTo(p2.getTitle());
            default -> 0;
        };
    }
}