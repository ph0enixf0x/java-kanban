package ru.yandex.practicum.manager;

public class Managers {

    public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
