package manager;

import model.Task;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager{
    private final Map<Integer, Node<Task>> customLinkedList = new HashMap<>();
    private Node<Task> first;
    private Node<Task> last;

    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    private void linkLast(Task task) {
        final Node<Task> lastNode = last;
        final Node<Task> newNode = new Node<>(lastNode, task, null);
        last = newNode;
        if (lastNode == null) {
            first = newNode;
        } else {
            lastNode.next = newNode;
        }
        customLinkedList.put(task.getId(), newNode);
    }

    private void removeNode(Node<Task> node) {
        if (node == first) {
            first = first.next;
            if (first != null) {
                first.prev = null;
            } else {
                last = null;
            }
        } else if (node == last) {
            last = last.prev;
            last.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    private List<Task> getTasks() {
        List<Task> result = new ArrayList<>();
        Node<Task> node = first;
        while (node != null) {
            result.add(node.item);
            node = node.next;
        }
        return result;
    }

    @Override
    public void add(Task task) {
        final Node<Task> node = customLinkedList.get(task.getId());
        if (node != null) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = customLinkedList.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
