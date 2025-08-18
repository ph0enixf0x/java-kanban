package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

public class Node {
    public Task data;
    public Node next;
    public Node prev;

    public Node(Task data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}
