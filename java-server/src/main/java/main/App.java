package main;

import com.influxdb.client.InfluxDBClient;
import config.Config;
import handler.event.consumer.TempEventConsumer;
import handler.event.producer.EventProducer;
import io.grpc.event.TempEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ds.EventQueue;
import service.EventService;
import service.Service;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

    private static final Logger logger = LogManager.getLogger(App.class);
    private static final int MAX_THREADS = 4; // Testing on quad-core Rasp Pi

    private InfluxDBClient client;
    private EventQueue<TempEvent> queue;
    private Service service;

    public static void main(String[] args) throws IOException {
        logger.info("Starting Application.");
        new App()
                .setupBeans()
                .startApp();
    }

    private void startApp() {
        // Create thread pool
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);
        Runnable producer = new EventProducer(queue);
        Runnable consumer = new TempEventConsumer(queue, service);
        pool.execute(consumer);
        pool.execute(producer);
    }

    private App setupBeans() {
        // Inject dependencies from Config
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        client = context.getBean(InfluxDBClient.class);
        queue = context.getBean(EventQueue.class);
        service = context.getBean(EventService.class);
        return this;
    }
}