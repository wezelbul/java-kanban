package mylinkedlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoryLinkedList<T> {

    private Node<T> first;
    private Node<T> last;

    private Map<Integer, Node<T>> idToNodeHashMap = new HashMap<>();

    public void linkLast(T t) {
        final Node<T> l = last;
        final Node<T> newNode = new Node<>(l, t, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
           newNode.getPrev().setNext(newNode);
        idToNodeHashMap.put(t.hashCode(), newNode);
    }

    public boolean contains(T t) {
        return idToNodeHashMap.containsKey(t.hashCode());
    }

    public void removeNode(Node<T> node) {
        if (node == null) {
            return;
        }
        Node<T> previous = node.getPrev();
        Node<T> next = node.getNext();
        if (node.equals(first) && !node.equals(last)) {
            if (next != null) {
                next.setPrev(null);
            }
            first = next;
        } else if (node.equals(last) && !node.equals(first)) {
            previous.setNext(null);
        } else if (!node.equals(first) && !node.equals(last)) {
            previous.setNext(next);
            if (next != null) {
                next.setPrev(previous);
            }
        } else {
            first = null;
            last = null;
        }
        Integer id = node.getItem().hashCode();
        idToNodeHashMap.remove(id, node);
        node = null;
        if (idToNodeHashMap.isEmpty()) {
            idToNodeHashMap = new HashMap<>();
        }
    }

    public Node<T> getNode(int id) {
        return idToNodeHashMap.get(id);
    }

    public ArrayList<T> getObjects() {
        if (idToNodeHashMap.isEmpty()) {
            return null;
        }
        ArrayList<T> arrayList = new ArrayList<>();
        if (first != null) {
            arrayList.add(first.getItem());
            Node<T> next = first.getNext();
            while (next != null) {
                arrayList.add(next.getItem());
                next = next.getNext();
            }
        }
        return arrayList;
    }

}