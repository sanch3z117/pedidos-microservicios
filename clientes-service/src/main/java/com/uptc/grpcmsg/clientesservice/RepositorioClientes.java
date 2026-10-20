package com.uptc.grpcmsg.clientesservice;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;

@Component
public class RepositorioClientes {

    private final Map<Long, String> clientes = Map.of(
            1L, "Jorge Otálora",
            2L, "Laura Gómez"
    );

    public Optional<String> buscarNombre(long id) {
        return Optional.ofNullable(clientes.get(id));
    }
}
