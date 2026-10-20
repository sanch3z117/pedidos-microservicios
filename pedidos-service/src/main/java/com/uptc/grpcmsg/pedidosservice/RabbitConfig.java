package com.uptc.grpcmsg.pedidosservice;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange("pedidos-exchange");
    }

    @Bean
    public Queue pedidosCreadosQueue() {
        return new Queue("pedidos.creados.queue");
    }

    @Bean
    public Binding binding(Queue pedidosCreadosQueue, TopicExchange pedidosExchange) {
        return BindingBuilder.bind(pedidosCreadosQueue).to(pedidosExchange).with("pedidos.creados");
    }
}
