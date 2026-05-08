# Documento de Arquitectura DDD - LogiFlow Fase 1

## 1. Análisis del problema actual del negocio

### 1.1. Contexto actual de LogiFlow

LogiFlow es una empresa de logística fundada hace doce años que ofrece servicios de paquetería y mensajería en tres niveles geográficos: local (entregas urbanas en 5 ciudades principales), provincial (rutas entre capitales de provincia y localidades cercanas) y nacional (transporte de carga consolidada entre distintas provincias). La flota de vehículos es heterogénea e incluye motocicletas, automóviles, furgonetas y camiones.

Actualmente, la empresa opera con un **sistema monolítico heredado** que centraliza todas las funciones: registro de clientes, alta de pedidos, asignación manual de vehículos, seguimiento esporádico mediante llamadas telefónicas, facturación y notificaciones por correo electrónico básicas.

### 1.2. Problemas del monolito actual

#### Acoplamiento extremo
El sistema monolítico actual presenta un acoplamiento severo entre todos sus componentes. La lógica de enrutamiento, gestión de pedidos, facturación y notificaciones reside en un único código base. **Cualquier cambio en la lógica de enrutamiento obliga a redesplegar toda la aplicación**, generando paradas completas del servicio y afectando todas las operaciones de la empresa.

#### Escalabilidad nula
La arquitectura monolítica no permite escalar componentes de forma independiente. Durante los **picos de demanda estacional** (Black Friday, campañas navideñas, Cyber Monday), el sistema se satura completamente, provocando:
- Lentitud extrema en la respuesta a usuarios y operadores
- Caídas parciales o totales del servicio
- Pérdida de facturación por pedidos no procesados
- Insatisfacción de clientes que recurren a la competencia

#### Falta de trazabilidad en tiempo real
Los clientes no pueden seguir sus paquetes en un mapa en tiempo real. El seguimiento actual se basa en:
- Anotaciones manuales por parte de operadores
- Llamadas telefónicas a conductores
- Actualizaciones esporádicas sin estandarización

Esta falta de transparencia genera constantes consultas al centro de atención al cliente, aumentando la carga operativa y deteriorando la experiencia del usuario.

#### Asignación ineficiente de pedidos
El proceso de emparejar pedidos con vehículos es **completamente manual**. Los operadores deben:
1. Revisar manualmente los pedidos pendientes
2. Consultar la disponibilidad de vehículos en hojas de cálculo
3. Estimar rutas sin herramientas de optimización
4. Asignar considerando solo criterios subjetivos

No se consideran factores críticos como optimización de rutas, tipo de vehículo más adecuado, ubicación actual de los vehículos o prioridad de los pedidos.


#### Notificaciones reactivas y pobres
Los cambios de estado de un pedido se comunican exclusivamente por correo electrónico y con retrasos significativos. No existen:
- Notificaciones push
- Notificaciones SMS
- Actualizaciones en tiempo real
- Múltiples canales de comunicación

#### Ciclo de despliegue lento y propenso a errores
Las actualizaciones se realizan mediante **FTP manual**, sin:
- Entornos de pruebas (staging) aislados
- Análisis de calidad de código automatizado
- Integración continua (CI)
- Pruebas automatizadas antes de producción
- Rollback automático en caso de fallo

Un despliegue típico requiere horas de trabajo, genera ventanas de mantenimiento prolongadas y frecuentemente introduce regresiones no detectadas.

### 1.3. Impacto en el negocio

| Problema | Impacto cuantitativo (estimado) |
|:---------|:-------------------------------|
| Caídas en picos de demanda | 15-20% de pedidos no procesados |
| Asignación manual ineficiente | 30% más de tiempo por envío |
| Falta de trazabilidad | 40% de llamadas al call center |
| Despliegues lentos | 12 horas promedio por release |
| Integración frágil con taller | 5 incidentes críticos por mes |

---

## 2. Event Storming (Modelado del dominio)

### 2.1. Eventos de dominio (Domain Events)

Los eventos representan **algo que ya ocurrió** en el dominio y es relevante para el negocio:

| Evento | Contexto origen | Descripción |
|:-------|:----------------|:------------|
| `PedidoCreado` | Pedidos | Un cliente ha registrado un nuevo pedido de envío |
| `PedidoAsignado` | Ruteo | Un pedido ha sido asignado a un vehículo específico |
| `PedidoEnRuta` | Ruteo | El vehículo ha iniciado el recorrido del pedido |
| `PedidoEntregado` | Ruteo | El paquete ha sido entregado al destinatario |
| `PedidoCancelado` | Pedidos | El pedido fue cancelado antes de ser entregado |
| `VehiculoDisponible` | Flota | Un vehículo cambia su estado a disponible |
| `VehiculoEnMantenimiento` | Flota/Taller | Un vehículo requiere o está en mantenimiento |
| `PosicionActualizada` | Seguimiento | Se recibe una nueva coordenada GPS de un vehículo |
| `RutaOptimizada` | Ruteo | Se ha calculado una nueva ruta optimizada |
| `OrdenMantenimientoRegistrada` | Taller | Se registró una orden en el sistema del taller externo |
| `FacturaEmitida` | Facturación | Se generó la factura por un servicio completado |
| `NotificacionEnviada` | Notificaciones | Se envió una notificación al cliente/operador |
| `ClienteRegistrado` | Clientes | Un nuevo cliente se registró en la plataforma |
| `ConductorAsignado` | Flota | Se asignó un conductor a un vehículo |

### 2.2. Comandos (Commands)

Los comandos representan **acciones que el usuario o el sistema inician**:

| Comando | Contexto destino | Descripción |
|:--------|:-----------------|:------------|
| `CrearPedido` | Pedidos | Registrar un nuevo pedido con origen, destino y paquete |
| `ActualizarEstadoPedido` | Pedidos | Cambiar el estado de un pedido existente |
| `AsignarPedidoAVehiculo` | Ruteo | Asignar un pedido a un vehículo disponible |
| `OptimizarRuta` | Ruteo | Calcular la ruta óptima para un conjunto de pedidos |
| `RegistrarPosicion` | Seguimiento | Guardar una nueva coordenada GPS de un vehículo |
| `RegistrarMantenimiento` | Taller | Crear una orden de mantenimiento en el sistema externo |
| `ConsultarVehiculoTaller` | Taller | Consultar datos de un vehículo en el sistema del taller |
| `RegistrarVehiculo` | Flota | Agregar un nuevo vehículo a la flota |
| `RegistrarConductor` | Flota | Agregar un nuevo conductor |
| `ActualizarDisponibilidadVehiculo` | Flota | Cambiar el estado operativo de un vehículo |
| `CalcularFactura` | Facturación | Generar la factura por los servicios prestados |
| `EnviarNotificacion` | Notificaciones | Enviar una notificación por un canal específico |
| `AutenticarUsuario` | Autenticación | Validar credenciales de un usuario |
| `ConsultarDisponibilidadFlota` | Flota | Consultar vehículos disponibles por tipo/capacidad |

### 2.3. Agregados raíz (Aggregates)

Los agregados son **entidades con consistencia transaccional**:

| Agregado | Contexto | Atributos clave | Invariantes |
|:---------|:---------|:----------------|:-------------|
| `Pedido` | Pedidos | id, origen, destino, paquete, estado, cliente, prioridad | El estado solo puede avanzar (no retroceder); no se puede modificar un pedido entregado |
| `Vehículo` | Flota | matrícula, tipo, capacidad, estado, ubicación actual | Capacidad > 0; estado válido según máquina de estados |
| `Conductor` | Flota | id, nombre, licencia, vehículoAsignado | Licencia compatible con tipo de vehículo |
| `Envío` | Ruteo | id, pedidoId, vehiculoId, ruta, paradas, horarios | Un pedido pertenece a un solo envío activo |
| `OrdenMantenimiento` | Taller | id, matrícula, descripción, fecha, estado | No crear orden duplicada activa para el mismo vehículo |
| `Factura` | Facturación | id, clienteId, pedidoId, monto, fecha, estado | El monto no puede ser negativo |
| `Cliente` | Clientes | id, nombre, email, teléfono, saldo, contrato | Email único; saldo no negativo |
| `Usuario` | Autenticación | id, username, rol, token | Username único; rol válido |

### 2.4. Diagrama de Event Storming (Mermaid)

```mermaid
flowchart TD
    subgraph "Comandos"
        C1[CrearPedido]
        C2[AsignarPedidoAVehiculo]
        C3[RegistrarPosicion]
        C4[RegistrarMantenimiento]
    end
    
    subgraph "Agregados"
        A1[Pedido]
        A2[Vehículo]
        A3[Envío]
        A4[OrdenMantenimiento]
    end
    
    subgraph "Eventos"
        E1[PedidoCreado]
        E2[PedidoAsignado]
        E3[PosicionActualizada]
        E4[OrdenMantenimientoRegistrada]
        E5[PedidoEntregado]
    end
    
    C1 -->|genera| E1
    E1 -->|dispara| C2
    C2 -->|genera| E2
    C3 -->|genera| E3
    C4 -->|genera| E4
    
    E2 -->|actualiza estado de| A3
    E3 -->|actualiza ubicación de| A2
    E4 -->|asocia a| A4

## 3. Identificación de dominios

### 3.1. Core Domain (Dominio principal)

**Gestión de la cadena de entrega**: Asignación inteligente de pedidos a vehículos, optimización de rutas y seguimiento en tiempo real.

**Justificación**: Este es el corazón competitivo del negocio de LogiFlow. La capacidad de asignar eficientemente pedidos, optimizar rutas y proporcionar trazabilidad en tiempo real es lo que diferencia a la empresa de sus competidores. Las inversiones deben concentrarse aquí.

**Componentes del Core Domain**:
- Ruteo y Asignación
- Seguimiento en tiempo real (WebSockets)

### 3.2. Subdominios de Soporte (Supporting Domains)

Son subdominios necesarios para el negocio, pero no proporcionan ventaja competitiva directa:

| Subdominio | Contexto asociado | Por qué es soporte |
|:-----------|:------------------|:-------------------|
| Gestión de Clientes | Clientes | Necesario pero commodity; CRUD estándar |
| Gestión de Flota | Flota (REST) | Crítico pero no diferenciador; gestión de activos |
| Gestión de Tarifas y Facturación | Facturación | Reglas de negocio estándar; pocas variaciones |
| Notificaciones | Notificaciones | Puede ser externalizado (SendGrid, Twilio) |

### 3.3. Subdominios Genéricos (Generic Subdomains)

Son dominios completamente commodity que podrían resolverse con soluciones externas o frameworks estándar:

| Subdominio | Contexto asociado | Posible solución externa |
|:-----------|:------------------|:-------------------------|
| Autenticación y autorización | Autenticación | Keycloak, Auth0, AWS Cognito |
| Integración con taller mecánico | Taller (SOAP) | Capa anticorrupción sobre SOAP legacy |

### 3.4. Tabla resumen de dominios

```mermaid
quadrantChart
    title "Clasificación de Dominios - LogiFlow"
    x-axis "Específico del negocio" --> "Commodity"
    y-axis "Bajo impacto estratégico" --> "Alto impacto estratégico"
    quadrant-1 "Core Domain"
    quadrant-2 "Genéricos"
    quadrant-3 "Soporte"
    quadrant-4 ""
    "Cadena de entrega": [0.85, 0.9]
    "Ruteo y Asignación": [0.8, 0.85]
    "Seguimiento": [0.75, 0.8]
    "Autenticación": [0.2, 0.6]
    "Taller SOAP": [0.25, 0.55]
    "Clientes": [0.5, 0.5]
    "Flota": [0.55, 0.6]
    "Facturación": [0.5, 0.45]
    "Notificaciones": [0.45, 0.4]