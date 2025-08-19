package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private final HashMap<Integer, Node> linkedHistory;

    public InMemoryHistoryManager() {
        this.head = null;
        this.tail = null;
        this.linkedHistory = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            System.out.println("При добавлении задачи в историю получен null");
            return;
        }
        int taskId = task.getId();
        if (linkedHistory.containsKey(taskId)) {
            removeNode(linkedHistory.get(taskId));
            linkedHistory.remove(taskId);
        }
        linkLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(getTasks());
    }

    @Override
    public void remove(int id) {
        if (!linkedHistory.containsKey(id)) {
            System.out.println("Такой задачи нет в истории");
            return;
        }
        Node removedNode = linkedHistory.get(id);
        removeNode(removedNode);
        linkedHistory.remove(id);
    }

    private void linkLast(Task task) {
        Node newHistoryEntry = new Node(task);
        int taskId = task.getId();
        if (head == null) {
            head = newHistoryEntry;
        } else {
            newHistoryEntry.prev = tail;
            tail.next = newHistoryEntry;
        }
        tail = newHistoryEntry;
        linkedHistory.put(taskId, newHistoryEntry);
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> collectedTasks = new ArrayList<>();
        Node workingNode = head;
        while (workingNode != null) {
            collectedTasks.add(workingNode.data);
            workingNode = workingNode.next;
        }
        return collectedTasks;
    }

    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;

        if (nextNode == null && prevNode == null) {
            head = null;
            tail = null;
        } else if (nextNode == null) {
            tail = prevNode;
            prevNode.next = null;
        } else if (prevNode == null) {
            head = nextNode;
            nextNode.prev = null;
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }
}
