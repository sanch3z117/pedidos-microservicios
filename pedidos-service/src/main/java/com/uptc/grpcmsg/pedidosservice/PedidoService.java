package com.uptc.grpcmsg.pedidosservice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final OutboxEventRepository outboxRepository;

    PedidoService(PedidoRepository pedidoRepository, OutboxEventRepository outboxRepository) {
        this.pedidoRepository = pedidoRepository;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public void crearPedido(String pedidoId, long clienteId) {
        pedidoRepository.save(new Pedido(pedidoId, clienteId, "CREADO"));

        String payload = """
                {"pedidoId": "%s", "cliente": "cliente-%d"}""".formatted(pedidoId, clienteId);
        outboxRepository.save(new OutboxEvent(pedidoId, "PedidoCreado", payload));
        // Ambas escrituras viven en el mismo COMMIT: si falla la transacción,
        // ni el pedido ni el evento quedan guardados.
    }

}
