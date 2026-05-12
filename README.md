🚚 LogiFlow – Plataforma Integral de Gestión Logística
Este repositorio contiene la primera fase de la transformación digital de LogiFlow, migrando un sistema monolítico hacia una arquitectura de microservicios basada en Domain-Driven Design (DDD).

🛠️ Arquitectura y Tecnologías
Estilo Arquitectónico: Microservicios independientes con bases de datos propias (Database per Service).

Backend: Java 21 con Spring Boot 3.

Base de Datos: PostgreSQL 16 (contenedorizado).

Documentación: OpenAPI 3 / Swagger UI.

DevOps: GitHub Actions, SonarCloud y notificaciones vía Telegram.

📦 Microservicios Implementados (Fase 1)
1. ms-flota-restA (Puerto: 8081)
Encargado de la gestión de activos y personal operativo.

Funcionalidad: CRUD completo de vehículos y conductores.

Seguridad: Validaciones estrictas de nombres (formato Capitalizado), bloqueo de tecleo errático y sanitización contra inyección SQL/XSS.

Endpoint de Negocio: Consulta de disponibilidad de vehículos para el contexto de ruteo.

2. ms-taller-restdos (Puerto: 8082)
Actúa como Anticorruption Layer (ACL) y servicio de soporte para mantenimiento.

Funcionalidad: Registro de órdenes de mantenimiento y consulta de datos técnicos de vehículos.

Integración: Consume de forma síncrona al ms-flota-restA mediante WebClient para actualizar el estado del vehículo a MANTENIMIENTO.

🚀 Instrucciones de Ejecución Local
Prerrequisitos
Docker y Docker Compose instalados.

JDK 21 instalado.

Maven 3.9+.

Paso 1: Levantar la Infraestructura
Desde la raíz del proyecto, ejecuta el siguiente comando para levantar las bases de datos PostgreSQL:

Bash
docker-compose up -d
Esto creará automáticamente las bases db_logiflow_flota y db_logiflow_taller en el puerto 5433.

Paso 2: Ejecutar los Microservicios
Ejecutar en orden (primero Flota, luego Taller):

Flota: Entrar a ms-flota-restA y ejecutar mvn spring-boot:run.

Taller: Entrar a ms-taller-restdos y ejecutar mvn spring-boot:run.

📑 Documentación de Endpoints (Swagger)
Accede a la documentación interactiva una vez levantados los servicios:

API Flota: http://localhost:8081/swagger-ui/index.html

API Taller: http://localhost:8082/swagger-ui/index.html

Ejemplo de Flujo de Negocio
POST /api/v1/vehiculos: Crear un vehículo con estado DISPONIBLE.

POST /api/v1/taller/ordenes-mantenimiento: Registrar incidencia.

Resultado: El taller guarda la orden y, automáticamente, el estado del vehículo en el microservicio de Flota cambia a MANTENIMIENTO.

🤖 Configuración del Pipeline CI/CD
El proyecto integra un flujo de Integración Continua automatizado:

Análisis Estático: Se ejecuta SonarCloud en cada push a la rama development para asegurar la calidad del código y detectar vulnerabilidades.

Notificaciones: Al finalizar el análisis, un bot de Telegram envía un reporte detallado al grupo del equipo indicando:

Estado de la construcción (Éxito/Fallo).

Microservicio afectado y autor del cambio.

Enlace directo a los logs del análisis.

Integrantes - Grupo ESPE:

Michael Coronado

Kevin Panata

Jhonny Mena

Docente: Ing. Geovanny Cudco