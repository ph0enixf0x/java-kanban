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
        }
        linkLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(getTasks());
    }

    private void linkLast(Task task) {
        Node newHistoryEntry = new Node(task);
        int taskId = task.getId();
        if (head == null) {
            head = newHistoryEntry;
            tail = newHistoryEntry;
        } else {
            newHistoryEntry.prev = tail;
            tail.next = newHistoryEntry;
            tail = newHistoryEntry;
        }
        linkedHistory.put(taskId, newHistoryEntry);
    }

    private ArrayList<Task> getTasks() {
        if (head == null) {
            System.out.println("История пустая!");
            return new ArrayList<>();
        }
        ArrayList<Task> collectedTasks = new ArrayList<>();
        Node workingNode = head;
        while (true) {
            collectedTasks.add(workingNode.data);
            if (workingNode.next == null) break;
            workingNode = workingNode.next;
        }
        return collectedTasks;
    }

    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;

        if (nextNode == null) {
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
