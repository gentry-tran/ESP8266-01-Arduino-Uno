package factory;

import org.springframework.stereotype.Component;
import wrapper.EventWrapper;

@Component
public class WrapperFactory<T> {
    public T wrap(T event) {
        return (T) new EventWrapper<>(event);
    }
}
