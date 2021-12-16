package service;

import io.grpc.event.TempEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ds.EventQueue;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EventProducer<E> implements Runnable {
    private static final Logger logger = LogManager.getLogger(EventProducer.class);
    private static final int PORT = 10101;
    private static final int SOCKET_TIMEOUT = 10000;
    private Socket client;

    final private EventQueue<E> queue;

    public EventProducer(EventQueue<E> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            if (client == null) {
                setClient();
            }
            synchronized (queue) {
                while (queue.size() > 2) {
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
                    event = TempEvent.parseFrom(client.getInputStream().readNBytes(12));
                } catch (IOException e) {
                    client = null;
                    logger.error(e.getStackTrace());
                }
                queue.add((E) event);
                logger.info(queue.size());
                queue.notifyAll();
            }
        }
    }

    private void setClient() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            logger.info("Listening on port: " + PORT);
            client = server.accept();
            logger.info("Client connected using remote port " + client.getPort());
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }
    }
}
