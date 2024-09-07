package manager;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    public InMemoryHistoryManager() {

    }

    private Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private HistoryLinkedList<Task> historyList = new HistoryLinkedList();

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }

    @Override
    public void addHistory(Task task) {
        if (task == null) {
            return;
        }
        Node<Task> node = historyMap.get(task.getId());
        if (node != null) {
            historyList.removeNode(node);
        }
        historyList.linkLast(task);
        Node<Task> newNode = historyList.getLast();
        historyMap.put(task.getId(), newNode);
    }

    @Override
    public void removeHistory (int id) {
        Node<Task> node = historyMap.get(id);
        if (node != null) {
            historyList.removeNode(node);
            historyMap.remove(id, node);
        }
    }

    @Override
    public void removeHistory (Set<Integer> ids) {
        for (Integer id : ids) {
            removeHistory(id);
        }
    }

    public class HistoryLinkedList<T> {
        private Node<T> last;

        public void linkLast(T element) {

            final Node<T> oldLast = last;
            final Node<T> newNode = new Node<>(oldLast, element, null);
            last = newNode;
            if (oldLast != null)
                oldLast.next = newNode;
        }

        public Node<T> getLast() {

            final Node<T> curLast = last;
            if (curLast == null)
                throw new NoSuchElementException();
            return curLast;
        }

        public List<T> getTasks() {
            List<T> tasksList = new ArrayList<>();
            Node<T> node = last;
            while (node != null) {
                tasksList.add(node.data);
                node = node.previous;
            }
            return tasksList;
        }

        public void removeNode (Node<T> node) {
            Node<T> previous = node.previous;
            Node<T> next = node.next;

            if (next != null) {
                next.previous = previous;
            } else {
                last = previous;
            }

            if (previous != null) {
                previous.next = next;
            }

            node.next = null;
            node.previous = null;
            node.data = null;
        }
    }
}
