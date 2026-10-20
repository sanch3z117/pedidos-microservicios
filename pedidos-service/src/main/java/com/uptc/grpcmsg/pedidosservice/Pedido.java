package com.uptc.grpcmsg.pedidosservice;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Pedido {

    @Id
    private String id;
    private long clienteId;
    private String estado;

    protected Pedido() {
    }

    public Pedido(String id, long clienteId, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.estado = estado;
    }

    public String getId() { return id; }
    public long getClienteId() { return clienteId; }
    public String getEstado() { return estado; }

}
