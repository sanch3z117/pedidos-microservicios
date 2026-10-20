package com.uptc.grpcmsg.cocinaservice;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoCreadoListener {

    @KafkaListener(topics = "pedidos.creados", groupId = "cocina-group")
    public void escuchar(String payload) {
        System.out.println("[cocina-service] Empezando a preparar: " + payload);
    }
}
