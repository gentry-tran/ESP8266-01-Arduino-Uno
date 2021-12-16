package factory;

import org.springframework.stereotype.Component;
import wrapper.Event;
import wrapper.EventWrapper;

@Component
public class WrapperFactory<T> {
    public Event wrap(T event) {
        return new EventWrapper<T>(event);
    }
}
