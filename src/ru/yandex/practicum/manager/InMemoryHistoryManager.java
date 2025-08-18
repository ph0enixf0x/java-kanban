package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history;
    private Node head;
    private Node tail;
    private ArrayList<Node> linkedHistory;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
        this.head = null;
        this.tail = null;
        this.linkedHistory = new ArrayList<>();
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
            linkedHistory.add(newHistoryEntry);
        } else {
            newHistoryEntry.prev = tail;
            tail.next = newHistoryEntry;
            tail = newHistoryEntry;
            linkedHistory.add(newHistoryEntry);
        }
    }

    public ArrayList<Task> getTasks() {
        if (head == null) {
            System.out.println("История пустая!");
            return null;
        }
        ArrayList<Task> collectedTasks = new ArrayList<>();
        for (Node node : linkedHistory) {
            collectedTasks.add(node.data);
        }
        return collectedTasks;
    }
}
