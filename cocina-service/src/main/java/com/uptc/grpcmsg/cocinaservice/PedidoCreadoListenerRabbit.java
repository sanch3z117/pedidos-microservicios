package com.uptc.grpcmsg.cocinaservice;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoCreadoListenerRabbit {

    @RabbitListener(queues = "pedidos.creados.queue")
    public void escuchar(String payload) {
        System.out.println("[cocina-service · RabbitMQ] Empezando a preparar: " + payload);
    }
}
