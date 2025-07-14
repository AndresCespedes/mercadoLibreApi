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
│   │   │       ├── config/             # Configuraciones de la aplicación
│   │   │       │   ├── LoggingInterceptor.java
│   │   │       │   └── WebConfig.java
│   │   │       ├── controller/         # Controladores REST
│   │   │       ├── dto/               # Objetos de transferencia de datos
│   │   │       │   ├── CreateProductRequest.java
│   │   │       │   ├── PagedResponse.java
│   │   │       │   ├── ProductSearchParams.java
│   │   │       │   └── UpdateProductRequest.java
│   │   │       ├── exception/         # Manejo de excepciones
│   │   │       ├── model/            # Entidades y modelos
│   │   │       │   ├── Category.java
│   │   │       │   ├── CreateProduct.java
│   │   │       │   ├── ProductRating.java
│   │   │       │   ├── Review.java
│   │   │       │   └── Seller.java
│   │   │       ├── repository/       # Capa de acceso a datos
│   │   │       ├── service/         # Lógica de negocio
│   │   │       ├── validation/      # Validadores personalizados
│   │   │       │   ├── RatingValidator.java
│   │   │       │   └── URLValidator.java
│   │   │       └── ProductApiApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── logback-spring.xml    # Configuración de logging
│   └── test/
│       └── java/
│           └── com/mercadolibre/product_api/
│               ├── controller/
│               ├── repository/
│               ├── service/
│               ├── validation/
│               └── performance/
└── pom.xml
```

## Configuración y Ejecución

1. Clonar el repositorio:
```bash
git clone https://github.com/AndresCespedes/mercadoLibreApi
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

La aplicación estará disponible en `http://localhost:{el puerto que configures}`

## Documentación de la API

La documentación de la API está disponible a través de Swagger UI:
- Swagger UI: `http://localhost:{el puerto que configures}/swagger-ui.html`
- OpenAPI JSON: `http://localhost:{el puerto que configures}/api-docs`

## Endpoints

### Productos

- `GET /api/products/{id}`: Obtiene un producto por su ID
- `GET /api/products`: Busca productos con filtros y paginación
- `POST /api/products`: Crea un nuevo producto
- `PUT /api/products/{id}`: Actualiza un producto existente
- `DELETE /api/products/{id}`: Elimina un producto

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

## Validaciones

El proyecto incluye validadores personalizados para:
- URLs de imágenes y sitios web
- Calificaciones de productos (1-5 estrellas)
- Datos de entrada mediante Jakarta Validation

## Logging y Monitoreo

- Logging detallado
- Interceptor personalizado para logging de requests/responses
- Configuración específica en `logback-spring.xml`
- Los logs se almacenan en `logs/product-api.log`

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


## Características

- Documentación completa con OpenAPI/Swagger
- Validación de datos con Jakarta Validation
- Manejo global de excepciones personalizado
- CORS habilitado para todos los orígenes
- Cobertura de código con JaCoCo
- Logging
- Paginación y filtros de búsqueda
- Ordenamiento flexible
- Pruebas de rendimiento con JMeter
- Validadores personalizados
- Interceptores para logging
- DTOs para requests/responses
- Manejo de errores consistente

## Documentación Técnica

Para más detalles sobre las decisiones de diseño, arquitectura y desafíos técnicos, consulta el archivo [TECHNICAL_DESIGN.md](TECHNICAL_DESIGN.md). 
