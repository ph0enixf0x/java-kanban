package ru.yandex.practicum;

import ru.yandex.practicum.tasks.*;

import java.util.HashMap;

public class TaskManager {
    private int taskCounter;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, SubTask> subTasks;
    private HashMap<Integer, Epic> epics;

    public TaskManager() {
        this.taskCounter = 1;
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public int getTaskCounter() {
        return taskCounter;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    private void increaseTaskCounter() {
        taskCounter += 1;
    }

    public void deleteTasks() {
        tasks = new HashMap<>();
    }

    public void deleteEpics() {
        epics = new HashMap<>();
    }

    public void deleteSubTasks() {
        subTasks = new HashMap<>();
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
        subTasks.put(subTask.getId(), subTask);
        increaseTaskCounter();
        epics.get(subTask.getEpicId()).addSubTask(subTask.getId());
    }
}
