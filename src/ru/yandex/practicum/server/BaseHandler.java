package ru.yandex.practicum.server;

import ru.yandex.practicum.manager.TaskManager;

public abstract class BaseHandler {
    protected TaskManager manager;

    public BaseHandler(TaskManager manager) {
        this.manager = manager;
    }
}
