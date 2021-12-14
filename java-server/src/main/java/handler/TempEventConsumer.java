package handler;

import io.grpc.event.TempEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pojo.EventQueue;

public class TempEventConsumer implements Runnable {
    private static final Logger logger = LogManager.getLogger(TempEventConsumer.class);

    final private EventQueue<TempEvent> queue;

    public TempEventConsumer(EventQueue<TempEvent> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        System.out.println("Queue is empty?");
                        logger.info("Queue is empty.");
                        queue.wait();
                    } catch (InterruptedException e) {
                        logger.error(e.getStackTrace());
                    }
                }
                TempEvent event = (TempEvent) queue.remove().event;
                System.out.println(event.getTemperature());
                int deviceId = event.getDeviceId();
                float humidity = event.getHumidity();
                float temperature = event.getTemperature();
                logger.info("Device Id: " + deviceId);
                logger.info("Humidity: " + humidity);
                logger.info("Temperature: " + temperature);
                queue.notifyAll();
            }
        }
    }
}
