package service;

import handler.TempEventConsumer;
import io.grpc.event.TempEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pojo.EventQueue;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final Logger logger = LogManager.getLogger(Server.class);
    private static final int MAX_THREADS = 4; // Testing on quad-core Rasp Pi

    public static void main(String[] args) throws IOException {
        logger.info("Starting Application.");
        // Create thread pool
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);
        EventQueue queue = new EventQueue<TempEvent>();
        Runnable producer = new EventProducer(queue);
        Runnable consumer = new TempEventConsumer(queue);

        pool.execute(producer);
        pool.execute(consumer);
    }
}
