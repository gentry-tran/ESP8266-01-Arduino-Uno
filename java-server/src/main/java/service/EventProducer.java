package service;

import io.grpc.event.TempEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pojo.EventQueue;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EventProducer<E> implements Runnable {
    private static final Logger logger = LogManager.getLogger(EventProducer.class);
    private final int port = 10101;

    //this holds queue instance coming from main thread
    final private EventQueue<E> queue;

    public EventProducer(EventQueue<E> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        synchronized (queue) {
            try (ServerSocket server = new ServerSocket(port)) {
                logger.info("Listening on port: " + port);
                while (true) {
                    try (Socket client = server.accept()) {
                        logger.info("Client connected using remote port " + client.getPort());

                        // Put events onto an event queue
                        TempEvent event = TempEvent.parseFrom(client.getInputStream().readNBytes(12));
                        queue.add((E) event);
                        queue.notifyAll();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
