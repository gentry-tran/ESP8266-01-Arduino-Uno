package service;

import com.influxdb.client.InfluxDBClient;
import config.Config;
import handler.event.TempEventConsumer;
import io.grpc.event.TempEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ds.EventQueue;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

    private static final Logger logger = LogManager.getLogger(App.class);
    private static final int MAX_THREADS = 4; // Testing on quad-core Rasp Pi

    private InfluxDBClient client;
    private EventQueue<TempEvent> queue;

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
        Runnable consumer = new TempEventConsumer(queue, client);
        pool.execute(consumer);
        pool.execute(producer);
    }

    private App setupBeans() {
        // Inject dependencies from Config
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        client = context.getBean(InfluxDBClient.class);
        queue = context.getBean(EventQueue.class);
        return this;
    }
}