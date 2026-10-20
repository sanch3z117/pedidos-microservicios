package com.uptc.grpcmsg.pedidosservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String clave;       // pedidoId: define la partición en Kafka
    private String tipo;        // "PedidoCreado"
    private String payload;     // JSON del evento
    private boolean publicado;  // false hasta que el publicador lo envía

    protected OutboxEvent() {
    }

    public OutboxEvent(String clave, String tipo, String payload) {
        this.clave = clave;
        this.tipo = tipo;
        this.payload = payload;
        this.publicado = false;
    }

    public Long getId() { return id; }
    public String getClave() { return clave; }
    public String getTipo() { return tipo; }
    public String getPayload() { return payload; }
    public boolean isPublicado() { return publicado; }

    public void marcarPublicado() {
        this.publicado = true;
    }

}
