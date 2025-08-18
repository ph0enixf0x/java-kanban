package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
        this.head = null;
        this.tail = null;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            System.out.println("При добавлении задачи в историю получен null");
            return;
        }
        if (history.size() >= 10) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }

    public void linkLast(Task task) {
        Node newHistoryEntry = new Node(task);
        if (head == null) {
            head = newHistoryEntry;
            tail = newHistoryEntry;
        } else {
            newHistoryEntry.prev = tail;
            tail.next = newHistoryEntry;
            tail = newHistoryEntry;
        }
    }

    public ArrayList<Task> getTasks() {
        if (head == null) {
            System.out.println("История пустая!");
            return null;
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
