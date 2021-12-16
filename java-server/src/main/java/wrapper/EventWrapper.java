package wrapper;

public class EventWrapper<T> implements Event {
    private T event;

    public EventWrapper(T eventType) {
        this.event = eventType;
    }

    public T getEvent() {
        return this.event;
    }
}
