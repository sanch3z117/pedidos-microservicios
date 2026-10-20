package com.uptc.grpcmsg.clientesservice;

import com.uptc.grpcmsg.clientesservice.grpc.ClienteGrpcGrpc;
import com.uptc.grpcmsg.clientesservice.grpc.ClienteRequest;
import com.uptc.grpcmsg.clientesservice.grpc.ClienteResponse;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class ClienteGrpcServiceImpl extends ClienteGrpcGrpc.ClienteGrpcImplBase {

    private final RepositorioClientes repositorio;

    ClienteGrpcServiceImpl(RepositorioClientes repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void existeCliente(ClienteRequest request, StreamObserver<ClienteResponse> responseObserver) {
        var nombre = repositorio.buscarNombre(request.getId());
        var respuesta = ClienteResponse.newBuilder()
                .setExiste(nombre.isPresent())
                .setNombreCompleto(nombre.orElse(""))
                .build();
        responseObserver.onNext(respuesta);
        responseObserver.onCompleted();
    }
}
