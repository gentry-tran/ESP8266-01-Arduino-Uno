package ds;

public class EventQueue<E> {
    public volatile Node head;
    public volatile Node tail;
    volatile int size = 0;

    public int size() {
        return this.size;
    }

    public Node getHead() {
        return this.head;
    }

    public Node getTail() {
        return this.tail;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void add(E dataEvent) {
        this.size++;
        Node event = new Node(dataEvent);
        event.next = this.head;
        this.head = event;
        if (tail == null) {
            tail = head;
        }
    }

    public Node remove() {
        if (head == null) {
            return null;
        }
        this.size--;
        Node event;
        Node current;
        if (head == tail) {
            event = head;
            head = tail = null;
            return event;
        } else {
            current = head;
            while (current.next != null && current.next.next != null) {
                current = current.next;
            }
            event = current.next;
            current.next = null;
            tail = current;
        }
        return event;
    }
}
