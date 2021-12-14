package pojo;

public class Node<E> {
    public E event;
    Node next;

    public Node() {
    }

    public Node(E dataEvent) {
        this.event = dataEvent;
    }
}
