package handler.event;

import com.influxdb.client.InfluxDBClient;
import io.grpc.event.TempEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ds.EventQueue;

@Service
public class TempEventConsumer implements Runnable {
    private static final Logger logger = LogManager.getLogger(TempEventConsumer.class);

    final private EventQueue<TempEvent> queue;

    @Autowired
    private InfluxDBClient client;

    @Autowired
    public TempEventConsumer(EventQueue<TempEvent> queue, InfluxDBClient client) {
        this.queue = queue;
        this.client = client;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        logger.info("Queue is empty.");
                        queue.wait();
                    } catch (InterruptedException e) {
                        logger.error(e.getStackTrace());
                    }
                }
                TempEvent event = (TempEvent) queue.remove().event;
                int deviceId = event.getDeviceId();
                float humidity = event.getHumidity();
                float temperature = event.getTemperature();

                // TODO post to influx
                client.makeWriteApi();
                logger.info("Device Id: " + deviceId);
                logger.info("Humidity: " + humidity);
                logger.info("Temperature: " + temperature);
                queue.notifyAll();
            }
        }
    }
}
