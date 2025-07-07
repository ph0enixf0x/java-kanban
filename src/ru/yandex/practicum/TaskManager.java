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

    public Task getTaskById(int id) {
        if(isExistingTask("Task", id)) return tasks.get(id);
        System.out.println("Несуществующий идентификатор задачи");
        return null;
    }

    public Epic getEpicById(int id) {
        if(isExistingTask("Epic", id)) return epics.get(id);
        System.out.println("Несуществующий идентификатор эпика");
        return null;
    }

    public SubTask getSubTaskById(int id) {
        if(isExistingTask("SubTask", id)) return subTasks.get(id);
        System.out.println("Несуществующий идентификатор подзадачи");
        return null;
    }

    private boolean isExistingTask(String type, int id) {
        return switch (type) {
            case "Task" -> tasks.containsKey(id);
            case "Epic" -> epics.containsKey(id);
            case "SubTask" -> subTasks.containsKey(id);
            default -> {
                System.out.println("Неизвестный тип задачи");
                yield false;
            }
        };
    }

    public void createTask(Task task) {
        int taskId = task.getId();
        if(!isExistingTask("Task", taskId)) {
            tasks.put(taskId, task);
            increaseTaskCounter();
        } else {
            System.out.println("Такая задача уже существует");
        }
    }

    public void createEpic(Epic epic) {
        int epicId = epic.getId();
        if(!isExistingTask("Epic", epicId)) {
            epics.put(epicId, epic);
            increaseTaskCounter();
        } else {
            System.out.println("Такой эпик уже существует");
        }
    }

    public void createSubTask(SubTask subTask) {
        int subTaskId = subTask.getId();
        if(!isExistingTask("SubTask", subTaskId)) {
            subTasks.put(subTaskId, subTask);
            increaseTaskCounter();
            epics.get(subTask.getEpicId()).addSubTask(subTaskId);
        } else {
            System.out.println("Такая подзадача уже существует");
        }
    }
}
