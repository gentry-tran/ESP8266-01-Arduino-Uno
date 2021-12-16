package handler.event.producer;

import ds.EventQueue;
import factory.WrapperFactory;
import io.grpc.event.TempEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EventProducer<E> implements Runnable {
    private static final Logger logger = LogManager.getLogger(EventProducer.class);
    private static final int PORT = 10101;
    private static final int SOCKET_TIMEOUT = 10000;
    private static final int CURRENT_PROTOBUF_BYTE_SIZE = 12; // bytes written
    private Socket client;

    final private EventQueue<E> queue;

    final private WrapperFactory factory;

    @Autowired
    public EventProducer(EventQueue<E> queue, WrapperFactory factory) {
        this.queue = queue;
        this.factory = factory;
    }

    @Override
    public void run() {
        while (true) {
            if (client == null) {
                setClient();
            }
            synchronized (queue) {
                while (queue.size() > 1) {
                    logger.info("Queue is filling.");
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // Put events onto an event queue
                TempEvent event = null;
                try {
                    // in case there is an interruption, prevent readNBytes from forever blocking
                    client.setSoTimeout(SOCKET_TIMEOUT);
                    event = TempEvent.parseFrom(client.getInputStream().readNBytes(CURRENT_PROTOBUF_BYTE_SIZE));
                    queue.add((E) factory.wrap(event));
                } catch (ClassCastException | IOException e) {
                    e.printStackTrace();
                }
                logger.info(queue.size());
                queue.notifyAll();
            }
        }
    }

    private void setClient() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            logger.info("Listening on port: " + PORT);
            client = server.accept();
            logger.info("Client connected using remote port: " + client.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}