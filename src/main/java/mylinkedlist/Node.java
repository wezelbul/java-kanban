package mylinkedlist;

public class Node<T> {
    private T item;
    private Node<T> next;
    private Node<T> prev;

    Node(Node<T> prev, T element, Node<T> next) {

        if (prev != null && element.equals(prev.item)) {
            this.prev = prev.getPrev();
            this.next = prev.getNext();
        } else {
            this.next = next;
            this.prev = prev;
        }
        this.item = element;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    public Node<T> getNext() {
        return this.next;
    }

    public Node<T> getPrev() {
        return this.prev;
    }

    public T getItem() {
        return this.item;
    }

}

