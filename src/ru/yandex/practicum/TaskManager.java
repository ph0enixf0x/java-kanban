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
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
    }

    public int getTaskCounter() {
        return taskCounter;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
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
        epics.get(subTask.getEpicId()).addSubTask(subTask.getId());
    }
}
