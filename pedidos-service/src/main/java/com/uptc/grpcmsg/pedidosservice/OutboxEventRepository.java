package com.uptc.grpcmsg.pedidosservice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByPublicadoFalseOrderByIdAsc();

}
