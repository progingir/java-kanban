package tracker;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> tasksById;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        tasksById = new HashMap<>();
        head = null;
        tail = null;
    }

    @Override
    public void add(Task task) {
        if (tasksById.containsKey(task.getId())) {
            remove(task.getId());
        }

        Node newNode = new Node(task);
        linkLast(newNode);
        tasksById.put(task.getId(), newNode);
    }

    public void remove(int id) {
        if (tasksById.containsKey(id)) {
            removeNode(tasksById.get(id));
            tasksById.remove(id);
        }
    }

    private void linkLast(Node node) {
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    public void clear() {
        tasksById.clear();
        head = null;
        tail = null;
    }

    private void removeNode(Node node) {
        if (node == head && node == tail) {
            head = null;
            tail = null;
        } else if (node == head) {
            head = node.next;
            if (head != null) {
                head.prev = null;
            }
        } else if (node == tail) {
            tail = node.prev;
            if (tail != null) {
                tail.next = null;
            }
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    public ArrayList<Task> getHistory() {
        ArrayList<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    private static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task) {
            this.task = task;
            this.prev = null;
            this.next = null;
        }
    }
}
