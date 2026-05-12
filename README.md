# LogiFlow - Microservicios listos para ejecutar

Incluye:

1. `ms-flota-rest` en puerto `8081`
2. `ms-taller-restdos` en puerto `8082`
3. PostgreSQL en Docker puerto `5433`

## Levantar base de datos

Desde esta carpeta:

```bash
docker compose up -d
```

Esto crea automáticamente:

- `db_logiflow_flota`
- `db_logiflow_taller`

## Ejecutar microservicios

Primero ejecuta `ms-flota-rest`.
Luego ejecuta `ms-taller-restdos`.

## Swagger

Flota:

```text
http://localhost:8081/swagger-ui/index.html
```

Taller RestDos:

```text
http://localhost:8082/swagger-ui/index.html
```

## Flujo recomendado

1. Crear vehículo en `ms-flota-rest`.
2. Consultarlo desde `ms-taller-restdos`.
3. Registrar orden de mantenimiento desde `ms-taller-restdos`.
4. El taller guarda la orden en su propia base y actualiza el estado del vehículo en flota a `MANTENIMIENTO`.
