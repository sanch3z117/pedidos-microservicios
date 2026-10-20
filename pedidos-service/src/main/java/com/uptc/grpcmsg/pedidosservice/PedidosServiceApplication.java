package com.uptc.grpcmsg.pedidosservice;

import com.uptc.grpcmsg.pedidosservice.grpc.ClienteGrpcGrpc.ClienteGrpcBlockingStub;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.grpc.client.ImportGrpcClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(proxyBeanMethods = false)
@EnableScheduling
@ImportGrpcClients(target = "clientes", types = ClienteGrpcBlockingStub.class)
public class PedidosServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PedidosServiceApplication.class, args);
    }

}
