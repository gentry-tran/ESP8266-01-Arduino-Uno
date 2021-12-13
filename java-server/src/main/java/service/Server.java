package service;

import io.grpc.event.TempEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 10101;
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Listening on port: " + port);

            while (true) {
                Socket client = server.accept();
                System.out.println("Client connected using remote port " + client.getPort());

                while (client.isConnected()) {
                    final Thread t = new Thread(() -> {
                        try {
                            byte[] event;
                            event = client.getInputStream().readNBytes(12);
                            TempEvent p = TempEvent.parseFrom(event);
                            int deviceId = p.getDeviceId();
                            float humidity = p.getHumidity();
                            float temperature = p.getTemperature();
                            System.out.println("Device Id :" + deviceId);
                            System.out.println("Humidity :" + humidity);
                            System.out.println("Temperature :" + temperature);
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    });
                    t.start();
                }
            }
        }
    }
}
