package service;

import handler.TemperatureClient;
import io.grpc.ServerBuilder;
import io.grpc.examples.helloworld.TempEvent;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Server {

//    private static volatile boolean stopped = false;
//
//    public static void main(String[] args) throws IOException, InterruptedException {
//        io.grpc.Server server = ServerBuilder
//                .forPort(8080)
//                .addService(new HelloServiceImpl()).build();
//        server.start();
//        server.awaitTermination();
//    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 8080;
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Listening on port: " + port);
//            TemperatureClient tempClient = new TemperatureClient();
            while(true) {
                Socket client = server.accept();
                System.out.println("Client connected using remote port " + client.getPort());
                final Thread t = new Thread(() -> {
                    try {
                        TempEvent p = TempEvent.parseFrom(client.getInputStream());
                        float i = p.getTempCel();
                        System.out.println("Temp " + i);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                });
                t.start();
            }
        }
    }
}
