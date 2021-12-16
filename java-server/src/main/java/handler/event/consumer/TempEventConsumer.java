package handler.event.consumer;

import ds.EventQueue;
import io.grpc.event.TempEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import service.EventService;
import service.Service;
import wrapper.Event;
import wrapper.EventWrapper;

public class TempEventConsumer implements Runnable {
    private static final Logger logger = LogManager.getLogger(TempEventConsumer.class);

    final private EventQueue<TempEvent> queue;

    private Service service;

    @Autowired
    public TempEventConsumer(EventQueue<TempEvent> queue, Service service) {
        this.queue = queue;
        this.service = service;
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
                        e.printStackTrace();
                    }
                }
                ((EventService)service).process((Event)queue.remove().event);
                queue.notifyAll();
            }
        }
    }
}
