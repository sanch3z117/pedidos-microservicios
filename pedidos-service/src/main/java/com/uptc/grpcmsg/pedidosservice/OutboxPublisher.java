package com.uptc.grpcmsg.pedidosservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    OutboxPublisher(OutboxEventRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publicarPendientes() {
        for (var evento : outboxRepository.findByPublicadoFalseOrderByIdAsc()) {
            try {
                // .get() espera la confirmación de Kafka: solo marcamos "publicado" si de verdad llegó.
                kafkaTemplate.send("pedidos.creados", evento.getClave(), evento.getPayload())
                        .get(5, TimeUnit.SECONDS);
                evento.marcarPublicado();
                log.info("Outbox: evento {} publicado en pedidos.creados", evento.getId());
            } catch (Exception e) {
                log.warn("Outbox: no se pudo publicar el evento {} (se reintentará): {}", evento.getId(), e.getMessage());
                return; // conserva el orden: no seguimos con los siguientes
            }
        }
    }

}
