package ds;

import wrapper.Event;

public interface SimpleQueue<E> {
    void add(E event);
    Node remove();
}
