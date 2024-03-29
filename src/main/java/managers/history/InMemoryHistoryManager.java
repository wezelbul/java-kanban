package managers.history;

import historylinkedlist.*;
import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private HistoryLinkedList<Task> historyLinkedList = new HistoryLinkedList<>();

    @Override
    public void add(Task task) {
        if (historyLinkedList.contains(task)) {
            remove(task.getId());
        }
        historyLinkedList.linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = historyLinkedList.getNode(id);
        historyLinkedList.removeNode(node);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyLinkedList.getObjects();
    }

}
