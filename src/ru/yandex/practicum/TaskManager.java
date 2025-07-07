package ru.yandex.practicum;

import ru.yandex.practicum.tasks.*;

import java.util.HashMap;

public class TaskManager {
    private int taskCounter;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, SubTask> subtasks;
    private HashMap<Integer, Epic> epics;

    public TaskManager() {
        this.taskCounter = 1;
    }

    public int getTaskCounter() {
        return taskCounter;
    }

    public void increaseTaskCounter() {
        taskCounter += 1;
    }

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
        increaseTaskCounter();
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        increaseTaskCounter();
    }

    public void createSubTask(SubTask subTask) {
        subtasks.put(subTask.getId(), subTask);
        increaseTaskCounter();
    }
}
