package com.mercadolibre.product_api.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.product_api.model.CreateProduct;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Repository
public class ProductRepository {
    
    private static final String DATA_FILE = "products.json";
    private final ObjectMapper objectMapper;
    private List<CreateProduct> products;
    
    public ProductRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.products = new ArrayList<>();
    }

    public ProductRepository() {
        this.objectMapper = new ObjectMapper();
        this.products = new ArrayList<>();
        // Constructor vacío para pruebas unitarias
    }
    
    @PostConstruct
    public void init() {
        loadData();
    }
    
    public Optional<CreateProduct> findById(String id) {
        return products.stream().filter(p -> Objects.equals(p.getId(), id)).findFirst();
    }
    
    public List<CreateProduct> findAll() {
        return new ArrayList<>(products);
    }

    public CreateProduct save(CreateProduct product) {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID().toString());
        }
        // Eliminar el producto existente si ya existe (por ID)
        products.removeIf(p -> Objects.equals(p.getId(), product.getId()));
        products.add(product);
        saveData();
        return product;
    }

    public boolean existsById(String id) {
        return products.stream().anyMatch(p -> Objects.equals(p.getId(), id));
    }

    public void deleteById(String id) {
        products.removeIf(p -> Objects.equals(p.getId(), id));
        saveData();
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try {
                products = objectMapper.readValue(file, new TypeReference<List<CreateProduct>>() {});
                log.info("Datos cargados exitosamente desde {}", DATA_FILE);
            } catch (IOException e) {
                log.error("Error al cargar datos desde {}: {}", DATA_FILE, e.getMessage());
                products = new ArrayList<>();
            }
        } else {
            log.warn("Archivo {} no encontrado. Iniciando con lista vacía.", DATA_FILE);
            products = new ArrayList<>();
        }
    }

    private void saveData() {
        try {
            objectMapper.writeValue(new File(DATA_FILE), products);
            log.info("Datos guardados exitosamente en {}", DATA_FILE);
        } catch (IOException e) {
            log.error("Error al guardar datos en {}: {}", DATA_FILE, e.getMessage());
        }
    }
} 