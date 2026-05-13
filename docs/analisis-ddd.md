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

![Diagrama de Event Storming](docs/images/event_storming.png)

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

| Dominio | Contexto | Impacto estratégico | Especificidad |
|:--------|:---------|:-------------------|:--------------|
| **Core** | Cadena de entrega (Ruteo + Seguimiento) | Alto | Muy específico |
| **Core** | Ruteo y Asignación | Alto | Muy específico |
| **Core** | Seguimiento | Alto | Específico |
| **Genérico** | Autenticación | Medio | Commodity |
| **Genérico** | Taller SOAP | Medio | Commodity |
| **Soporte** | Clientes | Bajo | Estándar |
| **Soporte** | Flota | Medio | Estándar |
| **Soporte** | Facturación | Bajo | Estándar |
| **Soporte** | Notificaciones | Bajo | Commodity |

## 4. Bounded Contexts y Lenguaje Ubicuo

Para cada contexto se define: **responsabilidad** y **lenguaje ubicuo** (mínimo 5 términos con su significado específico dentro de ese contexto).

### 4.1. Contexto: Pedidos

| Atributo | Valor |
|:---------|:------|
| **Responsabilidad** | Recepción, validación, gestión de estados y cierre de pedidos. |

**Lenguaje Ubicuo de Pedidos**:

| Término | Significado en este contexto |
|:--------|:-----------------------------|
| **Pedido** | Solicitud de envío que contiene origen, destino, paquete y prioridad. |
| **Origen** | Dirección completa donde se retira el paquete (incluye coordenadas). |
| **Destino** | Dirección completa donde debe entregarse el paquete. |
| **Paquete** | Objeto a transportar con peso, dimensiones y tipo de contenido. |
| **Estado** | Situación del pedido: CREADO, ASIGNADO, EN_RUTA, ENTREGADO, CANCELADO. |
| **Prioridad** | Nivel de urgencia: NORMAL, ALTA, EXPRESA (afecta asignación y tarifa). |

### 4.2. Contexto: Flota (REST)

| Atributo | Valor |
|:---------|:------|
| **Responsabilidad** | Administración de vehículos y conductores, disponibilidad, características técnicas. |

**Lenguaje Ubicuo de Flota**:

| Término | Significado en este contexto |
|:--------|:-----------------------------|
| **Vehículo** | Unidad de transporte con matrícula, tipo, capacidad y estado operativo. |
| **Conductor** | Persona física habilitada para operar un vehículo, con licencia válida. |
| **Capacidad (kg)** | Peso máximo en kilogramos que el vehículo puede transportar legalmente. |
| **Estado** | Situación operativa: DISPONIBLE, EN_SERVICIO, MANTENIMIENTO, INACTIVO. |
| **Tipo** | Clasificación del vehículo según uso y tamaño: MOTO, AUTO, FURGONETA, CAMION. |
| **Autonomía (km)** | Distancia máxima que el vehículo puede recorrer con combustible/carga actual. |

### 4.3. Contexto: Taller (SOAP)

| Atributo | Valor |
|:---------|:------|
| **Responsabilidad** | Capa anticorrupción (ACL) para exponer/interactuar con el sistema externo de talleres mecánicos. |

**Lenguaje Ubicuo de Taller**:

| Término | Significado en este contexto |
|:--------|:-----------------------------|
| **OrdenMantenimiento** | Solicitud de servicio técnico para un vehículo en el sistema del taller externo. |
| **Matrícula** | Identificador único del vehículo según registro vehicular oficial. |
| **Descripción** | Detalle de la avería, síntoma o servicio requerido. |
| **Fecha** | Fecha y hora programada o realizada del mantenimiento. |
| **EstadoMantenimiento** | Situación: PENDIENTE, EN_PROCESO, COMPLETADO, CANCELADO. |
| **DatosVehiculo** | Información técnica devuelta por el taller: marca, modelo, año, km. |

### 4.4. Contexto: Ruteo y Asignación

| Atributo | Valor |
|:---------|:------|
| **Responsabilidad** | Algoritmo de asignación de pedidos a vehículos, cálculo de rutas óptimas. |

**Lenguaje Ubicuo de Ruteo**:

| Término | Significado en este contexto |
|:--------|:-----------------------------|
| **Envío** | Asociación transaccional entre un pedido y un vehículo asignado. |
| **Ruta** | Secuencia ordenada de paradas que debe realizar un vehículo. |
| **Parada** | Punto geográfico (origen o destino) dentro de una ruta. |
| **Horario estimado** | Tiempo calculado de llegada (ETA) a cada parada. |
| **Kms** | Distancia total planificada de la ruta en kilómetros. |
| **Factor de optimización** | Métrica de eficiencia: minimizar distancia, tiempo o costo. |

### 4.5. Contexto: Seguimiento

| Atributo | Valor |
|:---------|:------|
| **Responsabilidad** | Ubicación en tiempo real de envíos activos (WebSockets para frontend). |

**Lenguaje Ubicuo de Seguimiento**:

| Término | Significado en este contexto |
|:--------|:-----------------------------|
| **Posición** | Coordenadas geográficas (latitud/longitud) actuales del vehículo. |
| **Velocidad** | Velocidad instantánea del vehículo en km/h. |
| **Tramo** | Segmento de ruta entre dos paradas consecutivas. |
| **ETA** | Tiempo estimado de llegada al destino final (Estimated Time of Arrival). |
| **Evento** | Suceso relevante durante el trayecto (salida, llegada, retraso, incidente). |
| **Geocerca** | Área virtual que activa eventos al entrar/salir (ej. radio 100m del destino). |

### 4.6. Contexto: Facturación

| Atributo | Valor |
|:---------|:------|
| **Responsabilidad** | Cálculo de costos por envío y emisión de facturas. |

**Lenguaje Ubicuo de Facturación**:

| Término | Significado en este contexto |
|:--------|:-----------------------------|
| **Tarifa** | Precio base por tipo de servicio, nivel geográfico y prioridad. |
| **Trayecto** | Distancia real recorrida entre origen y destino. |
| **Peso** | Peso efectivo del paquete (kg) que multiplica la tarifa base. |
| **Recargo** | Incremento por factores: horario nocturno, fin de semana, combustible. |
| **Factura** | Documento fiscal emitido por servicio completado. |
| **Saldo** | Crédito pendiente de pago por parte del cliente. |

### 4.7. Contexto: Clientes

| Atributo | Valor |
|:---------|:------|
| **Responsabilidad** | Datos maestros de clientes y cuentas corporativas. |

**Lenguaje Ubicuo de Clientes**:

| Término | Significado en este contexto |
|:--------|:-----------------------------|
| **Cliente** | Persona natural o jurídica que contrata servicios de envío. |
| **Cuenta** | Perfil comercial con datos de contacto, facturación y preferencias. |
| **Contrato** | Acuerdo comercial que define tarifas especiales y condiciones. |
| **Saldo** | Monto pendiente de pago o crédito disponible del cliente. |
| **Segmento** | Clasificación: INDIVIDUAL, CORPORATIVO, PREMIUM, FREQUENTE. |
| **Direcciones** | Listado de direcciones frecuentes guardadas por el cliente. |

### 4.8. Contexto: Autenticación

| Atributo | Valor |
|:---------|:------|
| **Responsabilidad** | Gestión de usuarios, roles, autenticación y generación de tokens. |

**Lenguaje Ubicuo de Autenticación**:

| Término | Significado en este contexto |
|:--------|:-----------------------------|
| **Usuario** | Entidad digital con credenciales para acceder al sistema. |
| **Rol** | Conjunto de permisos: CLIENTE, CONDUCTOR, OPERADOR, ADMIN. |
| **Token** | Credencial cifrada (JWT) que autoriza peticiones autenticadas. |
| **Login** | Proceso de identificación mediante usuario/contraseña. |
| **Permiso** | Operación específica permitida a un rol (ej. crear_pedido). |
| **Sesión** | Período de actividad de un usuario autenticado. |

### 4.9. Contexto: Notificaciones

| Atributo | Valor |
|:---------|:------|
| **Responsabilidad** | Consumidor de eventos que envía notificaciones push, email o SMS. |

**Lenguaje Ubicuo de Notificaciones**:

| Término | Significado en este contexto |
|:--------|:-----------------------------|
| **Evento** | Suceso de dominio que dispara una notificación. |
| **Destinatario** | Receptor de la notificación (email, teléfono, device token). |
| **Canal** | Medio de entrega: EMAIL, SMS, PUSH, WHATSAPP. |
| **Plantilla** | Formato predefinido del mensaje con placeholders. |
| **Estado envío** | Resultado del delivery: PENDIENTE, ENVIADO, FALLIDO, LEÍDO. |
| **Prioridad notificación** | Nivel de urgencia que define canal y retry policy. |

### 4.10. Contexto: GraphQL Gateway (BFF)

| Atributo | Valor |
|:---------|:------|
| **Responsabilidad** | Capa BFF (Backend For Frontend) que agrega datos de múltiples servicios y expone GraphQL. |

**Lenguaje Ubicuo de GraphQL Gateway**:

| Término | Significado en este contexto |
|:--------|:-----------------------------|
| **Query** | Operación de lectura que solicita campos específicos de uno o más servicios. |
| **Mutation** | Operación de escritura que modifica datos a través de servicios REST subyacentes. |
| **Resolver** | Función que traduce una petición GraphQL a llamadas a servicios específicos. |
| **Schema** | Definición tipada de las queries y mutations disponibles. |
| **Batch** | Agrupación de múltiples resolvers para evitar N+1 queries. |
| **Federation** | Composición de esquemas de múltiples servicios GraphQL (opcional). |



## 5. Context Map (Mapa de relaciones entre contextos)

![Context Map](docs/images/context_map.png)


### 5.1. Tabla de patrones de relación

| Patrón de relación | Contextos involucrados | Justificación técnica |
|:-------------------|:----------------------|:----------------------|
| **Partnership** | Flota ↔ Ruteo | Ambos contextos evolucionan juntos. La asignación requiere disponibilidad de flota y la flota necesita conocer la carga de ruteo. Cambios coordinados. |
| **Customer/Supplier** | Pedidos → Ruteo | Ruteo consume pedidos para asignarlos. Pedidos es el proveedor (supplier). Si Pedidos cambia su modelo, Ruteo se adapta. |
| **Customer/Supplier** | Flota → Ruteo | Ruteo consume disponibilidad de flota. Flota es proveedor. |
| **Customer/Supplier** | Ruteo → Seguimiento | Ruteo produce los envíos que Seguimiento consume para trackear posiciones. |
| **Conformist** | Pedidos → Facturación | Facturación se conforma al modelo de eventos de Pedidos. No fuerza sus propios conceptos, se adapta para evitar fricción. |
| **Anticorruption Layer (ACL)** | Taller ↔ Flota | El contexto Taller actúa como capa anticorrupción entre el modelo externo del taller (caótico, no estandarizado) y el modelo interno de Flota. Traduce, valida y protege. |
| **Open Host Service (OHS)** | Flota → todos | Flota expone una API REST bien documentada como servicio anfitrión abierto para todos los consumidores internos. |
| **Published Language** | Eventos asíncronos | Los eventos de dominio (ej. PedidoCreado, PosicionActualizada) actúan como lenguaje publicado que múltiples contextos pueden consumir. |
| **Separate Ways** | Taller vs Autenticación | No existe relación directa entre estos contextos. Evolucionan de forma completamente independiente. |

---

## 6. Justificación técnica de decisiones arquitectónicas

### 6.1. ¿Por qué microservicios?

| Beneficio | Explicación aplicada a LogiFlow |
|:----------|:--------------------------------|
| **Escalabilidad independiente** | El contexto Ruteo (Core Domain) escalará horizontalmente durante Black Friday, mientras que Facturación puede seguir con 1 instancia. Cada servicio escala según su demanda específica. |
| **Despliegue independiente** | Una mejora en el algoritmo de asignación (Ruteo) se despliega sin tocar Pedidos, Flota o Facturación. No hay "big bang releases". |
| **Aislamiento de fallos** | Si el servicio de Notificaciones falla, los pedidos siguen creándose y asignándose. Fallos localizados, no caídas generales. |
| **Equipos autónomos** | Cada contexto delimitado puede tener un equipo propietario (o una persona en este proyecto) con responsabilidad clara. |
| **Tecnología apropiada** | Ruteo puede usar algoritmos pesados (Python/Go), mientras que Flota es un CRUD simple (Node.js/Spring Boot). Cada servicio elige su stack. |
| **Frecuencia de cambio diferente** | Facturación cambia 2 veces al año (tarifas). Ruteo cambia cada sprint (optimizaciones). Microservicios reflejan ciclos de vida distintos. |

### 6.2. ¿Por qué RabbitMQ (comunicación asíncrona)?

| Beneficio | Explicación aplicada a LogiFlow |
|:----------|:--------------------------------|
| **Desacoplamiento en el tiempo** | Pedidos publica PedidoCreado y no espera a que Facturación o Notificaciones procesen inmediatamente. Si Facturación está caída, el mensaje persiste en la cola. |
| **Tolerancia a fallos** | El bus de eventos (RabbitMQ) con colas duraderas garantiza que ningún evento se pierda incluso si consumidores están offline temporalmente. |
| **Escalabilidad de consumidores** | Si hay 10,000 notificaciones por enviar, se pueden instanciar 5 consumidores de Notificaciones en paralelo (competidores en cola). |
| **Propagación de eventos** | Un solo evento PedidoEntregado puede notificar a Facturación (genera factura), Notificaciones (avisa al cliente) y Analytics (actualiza métricas). |
| **Reducción de acoplamiento** | Ruteo no necesita conocer los detalles de cómo factura o notifica. Solo publica eventos. El core domain se mantiene limpio. |

### 6.3. ¿Por qué REST, GraphQL y WebSockets?

| Tecnología | Uso en LogiFlow | Justificación |
|:-----------|:----------------|:--------------|
| **REST** | Comunicación síncrona entre microservicios internos | Estándar moderno, simple, cacheable, stateless. Ideal para operaciones CRUD y consultas de disponibilidad. OpenAPI permite documentación interactiva. |
| **HTTP** | Integración con el sistema de taller mecánico externo | El taller externo expone APIs REST/HTTP estándar. Mantenemos compatibilidad mediante una Capa Anticorrupción que traduce el modelo externo al interno de Flota. |
| **GraphQL** | BFF para el frontend | El frontend necesita datos agregados de múltiples servicios (Pedido + Posición + Conductor). GraphQL evita over-fetching y under-fetching. |
| **WebSockets** | Seguimiento en tiempo real | Los clientes esperan ver la posición del vehículo en un mapa actualizándose en tiempo real. REST no es apropiado para streaming. |

### 6.4. Tabla resumen de decisiones técnicas

| Decisión | Alternativa descartada | Razón del descarte |
|:---------|:----------------------|:-------------------|
| Microservicios | Monolito | No resuelve escalabilidad ni independencia (problemas actuales) |
| RabbitMQ | HTTP síncrono | Introduciría acoplamiento temporal y fallos en cascada |
| Bases de datos por servicio | BD única compartida | Acoplamiento de esquemas; viola autonomía de microservicios |
| API Gateway (Kong/Nginx) | GraphQL Gateway puro | GraphQL Gateway es BFF; en Fase 2+ se añadirá gateway tradicional para auth/routing |

---

## 7. Conclusión de la Fase 1

El análisis DDD realizado ha permitido:

1. **Comprender el dominio actual** de LogiFlow y sus problemas críticos (monolito, escalabilidad, trazabilidad, integración frágil).
2. **Identificar el Core Domain** (Cadena de entrega: ruteo, asignación, seguimiento) donde concentrar la inversión estratégica.
3. **Delimitar 10 Bounded Contexts** con responsabilidades claras y lenguajes ubicuos.
4. **Definir un Context Map** con patrones Partnership, Customer/Supplier, Conformist y Anticorruption Layer.
5. **Justificar técnicamente** el uso de microservicios, RabbitMQ, REST, GraphQL y WebSockets.

La **Fase 1** implementará los pilotos técnicos de:
- `ms-flota-rest` (contexto Flota)
- `ms-taller-rest` (contexto Taller como ACL para integración con taller externo)

demostrando la viabilidad de la arquitectura propuesta.

---

**Firmado**:  
*Proyecto LogiFlow - Arquitectura de Software*  
*Fecha: Mayo 2026*

---

## Apéndice A: Tabla de correspondencia problemas vs soluciones

| Problema actual | Solución arquitectónica | Contexto(s) involucrado(s) |
|:----------------|:------------------------|:---------------------------|
| Acoplamiento extremo | Microservicios + Bounded Contexts | Todos |
| Escalabilidad nula | Escalamiento horizontal por servicio | Ruteo, Seguimiento |
| Falta de trazabilidad | WebSockets + eventos de posición | Seguimiento, Ruteo |
| Asignación manual | Algoritmo automático en contexto Ruteo | Ruteo |
| Integración frágil con taller externo | Capa Anticorrupción (ACL) | Taller, Flota |
| Notificaciones pobres | Event-driven + múltiples canales | Notificaciones |
| Despliegue FTP manual | CI/CD + GitHub Actions + Kubernetes | Infraestructura |

## Apéndice B: Glosario unificado del dominio LogiFlow

Este glosario consolida términos que aparecen en múltiples contextos:

| Término | Definición unificada |
|:--------|:---------------------|
| **Pedido** | Solicitud de transporte desde un origen a un destino. |
| **Envío** | Ejecución concreta de un pedido asignado a un vehículo y conductor. |
| **Vehículo** | Recurso físico de transporte con capacidad y autonomía. |
| **Ruta** | Secuencia planificada de paradas optimizada para eficiencia. |
| **Posición** | Coordenada geográfica en un momento específico. |
| **Evento** | Suceso de dominio relevante que se publica al bus de eventos. |