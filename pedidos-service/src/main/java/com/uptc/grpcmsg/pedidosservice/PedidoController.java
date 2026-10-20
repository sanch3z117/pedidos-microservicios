package com.uptc.grpcmsg.pedidosservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PedidoController {

    private final VerificadorCliente verificadorCliente;
    private final PedidoEventPublisher publisher;
    private final PedidoEventPublisherRabbit publisherRabbit;
    private final PedidoService pedidoService;

    PedidoController(VerificadorCliente verificadorCliente,
                     PedidoEventPublisher publisher,
                     PedidoEventPublisherRabbit publisherRabbit,
                     PedidoService pedidoService) {
        this.verificadorCliente = verificadorCliente;
        this.publisher = publisher;
        this.publisherRabbit = publisherRabbit;
        this.pedidoService = pedidoService;
    }

    @PostMapping("/api/pedidos")
    public ResponseEntity<String> crearPedido(@RequestParam long clienteId, @RequestParam String pedidoId) {
        if (!verificadorCliente.existe(clienteId)) {
            return ResponseEntity.status(404).body("Cliente no encontrado");
        }
        publisher.publicarPedidoCreado(pedidoId, "cliente-" + clienteId);
        return ResponseEntity.ok("Pedido " + pedidoId + " creado y evento publicado");
    }

    @PostMapping("/api/pedidos-rabbit")
    public ResponseEntity<String> crearPedidoRabbit(@RequestParam long clienteId, @RequestParam String pedidoId) {
        if (!verificadorCliente.existe(clienteId)) {
            return ResponseEntity.status(404).body("Cliente no encontrado");
        }
        publisherRabbit.publicarPedidoCreado(pedidoId, "cliente-" + clienteId);
        return ResponseEntity.ok("Pedido " + pedidoId + " creado (RabbitMQ)");
    }

    @PostMapping("/api/pedidos-outbox")
    public ResponseEntity<String> crearPedidoConOutbox(@RequestParam long clienteId, @RequestParam String pedidoId) {
        if (!verificadorCliente.existe(clienteId)) {
            return ResponseEntity.status(404).body("Cliente no encontrado");
        }
        pedidoService.crearPedido(pedidoId, clienteId);
        return ResponseEntity.ok("Pedido " + pedidoId + " guardado; el evento saldra por el outbox");
    }

}
