# API de Productos - MercadoLibre Challenge

Este proyecto es una API REST que proporciona información detallada de productos, inspirada en MercadoLibre.

## Tecnologías Utilizadas

- Java 17
- Spring Boot 3.2.3
- Maven
- JUnit 5
- Springdoc OpenAPI (Swagger)
- Lombok
- Jackson
- Apache JMeter

## Requisitos

- Java 17 o superior
- Maven 3.6 o superior

## Estructura del Proyecto

```
product-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/mercadolibre/product_api/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── exception/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── ProductApiApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/mercadolibre/product_api/
│               ├── controller/
│               ├── service/
│               └── performance/
└── pom.xml
```

## Configuración y Ejecución

1. Clonar el repositorio:
```bash
git clone <url-del-repositorio>
```

2. Navegar al directorio del proyecto:
```bash
cd product-api
```

3. Compilar el proyecto:
```bash
./mvnw clean install
```

4. Ejecutar la aplicación:
```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## Documentación de la API

La documentación de la API está disponible a través de Swagger UI:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## Endpoints

### Productos

- `GET /api/products/{id}`: Obtiene un producto por su ID
- `GET /api/products`: Busca productos con filtros y paginación

### Parámetros de Búsqueda

- `query`: Término de búsqueda en título y descripción
- `minPrice`: Precio mínimo
- `maxPrice`: Precio máximo
- `isOfficialStore`: Filtrar por tienda oficial
- `minRating`: Calificación mínima
- `page`: Número de página (desde 0)
- `size`: Tamaño de página
- `sortBy`: Campo para ordenar (id, price, rating)
- `sortDirection`: Dirección del ordenamiento (asc, desc)

Ejemplo:
```
GET /api/products?query=samsung&minPrice=400&maxPrice=1000&isOfficialStore=true&page=0&size=10&sortBy=price&sortDirection=asc
```

## Almacenamiento de Datos

Los datos se almacenan en un archivo JSON local (`products.json`) que se crea automáticamente en la raíz del proyecto.

## Tests

### Tests Unitarios y de Integración
```bash
./mvnw test
```

### Tests de Rendimiento
```bash
./mvnw test -Dtest=ProductAPIPerformanceTest
```

Los resultados de las pruebas de rendimiento se guardan en:
- Plan de pruebas: `target/jmeter/testplan.jmx`
- Resultados: `target/jmeter/results_[timestamp].jtl`

### Cobertura de Código
```bash
./mvnw verify
```
El reporte de cobertura estará disponible en `target/site/jacoco/index.html`

## Características

- Documentación completa con OpenAPI/Swagger
- Validación de datos con Jakarta Validation
- Manejo global de excepciones
- CORS habilitado para todos los orígenes
- Cobertura de código con JaCoCo
- Logging con SLF4J
- Paginación y filtros de búsqueda
- Ordenamiento flexible
- Pruebas de rendimiento con JMeter

## Documentación Técnica

Para más detalles sobre las decisiones de diseño, arquitectura y desafíos técnicos, consulta el archivo [TECHNICAL_DESIGN.md](TECHNICAL_DESIGN.md). 