package service;

import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.HelloResponse;
import io.grpc.examples.helloworld.HelloServiceGrpc;
import io.grpc.examples.helloworld.TempEvent;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

    @Override
    public void sayHello(
            HelloRequest request, StreamObserver<HelloResponse> responseObserver) {

        String greeting = new StringBuilder()
                .append("Hello, ")
                .append(request.getName())
                .toString();

        HelloResponse response = HelloResponse.newBuilder()
                .setMessage(greeting)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void postEvent(
            TempEvent event, StreamObserver<HelloResponse> responseObserver) {

        System.out.println(event.getTempCel());

        HelloResponse response = HelloResponse.newBuilder()
                .setMessage("")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}