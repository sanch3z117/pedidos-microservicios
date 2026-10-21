# pedidos-microservicios

Mini-curso complementario de la Lección 6: gRPC, Kafka, RabbitMQ y Transactional Outbox en la práctica.

## Servicios

| Servicio | HTTP | gRPC | Rol |
| --- | --- | --- | --- |
| `clientes-service` | 18080 | 19090 | Expone `ClienteGrpc/ExisteCliente` con dos clientes en memoria. |
| `pedidos-service` | 18081 | — | Verifica el cliente por gRPC y publica `PedidoCreado` por Kafka o RabbitMQ. |
| `cocina-service` | 18082 | — | Consume `PedidoCreado` desde Kafka y desde RabbitMQ. |

Los puertos no son los del enunciado (8080/8081/8082, 9090, 9092, 5672) para no chocar con los contenedores de `rica-api` que pueden estar corriendo en la misma máquina.

## Infraestructura aislada

| Componente | Contenedor | Puerto host |
| --- | --- | --- |
| Kafka (KRaft) | `pedidos-kafka` | 19092 |
| RabbitMQ | `pedidos-rabbitmq` | 5673 (AMQP), 15673 (consola) |

```powershell
docker compose up -d
docker exec pedidos-kafka /opt/kafka/bin/kafka-topics.sh --create --if-not-exists --topic pedidos.creados --partitions 3 --replication-factor 1 --bootstrap-server localhost:19092
```

Consola de RabbitMQ: http://localhost:15673 (usuario `guest`, clave `guest`).

## Ejecutar los servicios

En tres terminales, desde cada carpeta:

```powershell
cd clientes-service
.\mvnw.cmd spring-boot:run

cd cocina-service
.\mvnw.cmd spring-boot:run

cd pedidos-service
.\mvnw.cmd spring-boot:run
```

## Colección Bruno

Abre la carpeta `bruno/pedidos-microservicios` en Bruno (Open Collection) y selecciona el entorno `Local`.

| Carpeta | Request | Qué prueba |
| --- | --- | --- |
| `gRPC` | Cliente existente / inexistente | `ClienteGrpc/ExisteCliente` en 19090 (usa reflexión) |
| `Kafka` | Crear pedido | `PedidoCreado` por Kafka |
| `RabbitMQ` | Crear pedido | `PedidoCreado` por RabbitMQ |
| `Outbox` | Crear pedido con outbox | Publicación asíncrona y tolerancia a Kafka caído |

Los requests incluyen asserts de estado y cuerpo, así que también puedes correr la colección completa desde Bruno.

Las variables (`pedidosUrl`, `grpcClientes`, etc.) están definidas en `collection.bru` con `vars:pre-request`, así funcionan aunque el selector de entorno esté en `None`. `environments/Local.bru` repite los mismos valores para poder cambiarlos por entorno. Los gRPC usan además `protos/cliente.proto` vía `protoPath`. Ojo con el bloque `content`:

```text
content: '''
    {
      "id": 1
    }
  '''
```

Las líneas del JSON deben llevar **4 espacios** de indentación: el parser de `.bru` recorta exactamente 4 caracteres por línea y, con menos, elimina las llaves y el mensaje queda inválido.

## Pruebas

```powershell
# gRPC: cliente existente
$json = '{"id": 1}'
$json | docker run --rm -i fullstorydev/grpcurl -plaintext -d '@' host.docker.internal:19090 ClienteGrpc/ExisteCliente

# gRPC: cliente inexistente
$json = '{"id": 99}'
$json | docker run --rm -i fullstorydev/grpcurl -plaintext -d '@' host.docker.internal:19090 ClienteGrpc/ExisteCliente

# Kafka
curl.exe -X POST "http://localhost:18081/api/pedidos?clienteId=1&pedidoId=P-1001"

# RabbitMQ
curl.exe -X POST "http://localhost:18081/api/pedidos-rabbit?clienteId=2&pedidoId=P-2002"

# Transactional Outbox
curl.exe -X POST "http://localhost:18081/api/pedidos-outbox?clienteId=1&pedidoId=P-4001"
```

Resultados esperados:

- gRPC con `id=1`: `{ "existe": true, "nombreCompleto": "Jorge Otálora" }`.
- gRPC con `id=99`: `{}` (los valores por defecto de protobuf no se imprimen).
- Kafka: `cocina-service` imprime `[cocina-service] Empezando a preparar: {"pedidoId": "P-1001", "cliente": "cliente-1"}`.
- RabbitMQ: `cocina-service` imprime `[cocina-service · RabbitMQ] Empezando a preparar: ...`.
- Outbox: `pedidos-service` imprime `Outbox: evento N publicado en pedidos.creados` y `cocina-service` recibe el pedido.

## Transactional Outbox

`PedidoService.crearPedido` guarda el pedido y el evento en la misma transacción (`@Transactional`). `OutboxPublisher` revisa cada 2 segundos los eventos pendientes y los envía a Kafka; solo los marca como publicados cuando Kafka confirma.

Para comprobar la garantía:

```powershell
docker compose stop kafka
curl.exe -X POST "http://localhost:18081/api/pedidos-outbox?clienteId=1&pedidoId=P-4002"
docker compose start kafka
```

El pedido responde igualmente 200 y el evento llega a `cocina-service` cuando Kafka vuelve.

La base H2 queda en `pedidos-service/data/pedidos`. Consulta de evidencia:

```powershell
SELECT id, clave, tipo, publicado FROM outbox_event ORDER BY id;
SELECT id, cliente_id, estado FROM pedido ORDER BY id;
```
