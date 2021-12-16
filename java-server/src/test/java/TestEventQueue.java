import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.event.TempEvent;
import org.junit.jupiter.api.*;
import ds.EventQueue;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestEventQueue {

    EventQueue queue;

    @BeforeEach
    public void setup() {
        queue = new EventQueue();
    }

    @AfterEach
    public void tearDown() {
        queue = null;
    }

    @Test
    public void emptyListReturnsNull() throws InterruptedException {
        assertNull(queue.remove());
    }

    @Test
    public void canAddNodeToEmptyList() throws InvalidProtocolBufferException {
        TempEvent event = TempEvent.getDefaultInstance();
        queue.add(event);

        assertNotNull(queue.head);
        assertNotNull(queue.tail);
        Assertions.assertTrue(queue.head == queue.tail);
        Assertions.assertTrue(queue.size() == 1);
    }

    @Test
    public void queueReturnsEventsFIFO() throws InterruptedException {
        TempEvent event1 = TempEvent.getDefaultInstance();
        TempEvent event2 = TempEvent.getDefaultInstance();
        TempEvent event3 = TempEvent.getDefaultInstance();
        queue.add(event1);
        queue.add(event2);
        queue.add(event3);

        Assertions.assertTrue(queue.remove().event.equals(event1));
        Assertions.assertTrue(queue.remove().event.equals(event2));
        Assertions.assertTrue(queue.remove().event.equals(event3));
    }
}
