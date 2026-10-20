package com.uptc.grpcmsg.pedidosservice;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PedidoEventPublisherRabbit {

    private final RabbitTemplate rabbitTemplate;

    PedidoEventPublisherRabbit(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarPedidoCreado(String pedidoId, String clienteNombre) {
        String payload = """
            {"pedidoId": "%s", "cliente": "%s"}""".formatted(pedidoId, clienteNombre);
        rabbitTemplate.convertAndSend("pedidos-exchange", "pedidos.creados", payload);
    }
}
