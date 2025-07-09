package ru.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int taskCounter;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, SubTask> subTasks;
    private final HashMap<Integer, Epic> epics;

    public TaskManager() {
        this.taskCounter = 1;
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    private void increaseTaskCounter() {
        taskCounter += 1;
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subTasks.clear();
    }

    public void deleteSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            updateEpicStatus(epic.getId());
        }
    }

    public Task getTaskById(int taskId) {
        if (isExistingTask("Task", taskId)) return tasks.get(taskId);
        System.out.println("Задачи с идентификатором " + taskId + " не существует");
        return null;
    }

    public Epic getEpicById(int epicId) {
        if (isExistingTask("Epic", epicId)) return epics.get(epicId);
        System.out.println("Эпика с идентификатором " + epicId + " не существует");
        return null;
    }

    public SubTask getSubTaskById(int subTaskId) {
        if (isExistingTask("SubTask", subTaskId)) return subTasks.get(subTaskId);
        System.out.println("Подзадачи с идентификатором " + subTaskId + " не существует");
        return null;
    }

    public int createTask(Task task) {
        task.setId(taskCounter);
        tasks.put(taskCounter, task);
        increaseTaskCounter();
        return task.getId();
    }

    public int createEpic(Epic epic) {
        epic.setId(taskCounter);
        epics.put(taskCounter, epic);
        increaseTaskCounter();
        return epic.getId();
    }

    public int createSubTask(SubTask subTask) {
        subTask.setId(taskCounter);
        int epicId = subTask.getEpicId();
        if (!isExistingTask("Epic", epicId)) {
            System.out.println("Эпика с идентификатором " + epicId + " указанным в подзадаче не существует");
            return 0;
        }
        subTasks.put(taskCounter, subTask);
        epics.get(epicId).addSubTask(taskCounter);
        updateEpicStatus(epicId);
        increaseTaskCounter();
        return subTask.getId();
    }

    public void updateTask(Task task) {
        int taskId = task.getId();
        if (!isExistingTask("Task", taskId)) {
            System.out.println("Задачи с идентификатором " + taskId + " не существует");
            return;
        }
        tasks.put(taskId, task);
    }

    public void updateEpic(Epic epic) {
        int epicId = epic.getId();
        if (!isExistingTask("Epic", epicId)) {
            System.out.println("Эпика с идентификатором " + epicId + " не существует");
            return;
        }
        Epic updatedEpic = epics.get(epicId);
        updatedEpic.setName(epic.getName());
        updatedEpic.setDescription(epic.getDescription());
    }

    public void updateSubTask(SubTask subTask) {
        int subTaskId = subTask.getId();
        if (!isExistingTask("SubTask", subTaskId)) {
            System.out.println("Подзадачи с идентификатором " + subTaskId + " не существует");
            return;
        }
        subTasks.put(subTaskId, subTask);
        updateEpicStatus(subTask.getEpicId());
    }

    public void deleteTaskById(int taskId) {
        if (!tasks.containsKey(taskId)) {
            System.out.println("Задачи с идентификатором " + taskId + " не существует");
        }
        tasks.remove(taskId);
    }

    public void deleteEpicById(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпика с идентификатором " + epicId + " не существует");
            return;
        }
        for (int subtaskId : epics.get(epicId).getSubtasksIds()) {
            subTasks.remove(subtaskId);
        }
        epics.remove(epicId);
    }

    public void deleteSubTaskById(int subTaskId) {
        if (!subTasks.containsKey(subTaskId)) {
            System.out.println("Подзадачи с идентификатором " + subTaskId + " не существует.");
        }
        int epicId = subTasks.get(subTaskId).getEpicId();
        epics.get(epicId).removeSubTask(subTaskId);
        updateEpicStatus(epicId);
        subTasks.remove(subTaskId);
    }

    public ArrayList<SubTask> getEpicSubTasks(int id) {
        Epic epic = getEpicById(id);
        ArrayList<SubTask> result = new ArrayList<>();
        if (epic != null) {
            ArrayList<Integer> subtaskIds = epic.getSubtasksIds();
            for (int subtaskId : subtaskIds) {
                result.add(subTasks.get(subtaskId));
            }
            return result;
        }
        System.out.println("Эпика с идентификатором " + id + " не существует");
        return null;
    }

    private boolean isExistingTask(String type, int id) {
        return switch (type) {
            case "Task" -> tasks.containsKey(id);
            case "Epic" -> epics.containsKey(id);
            case "SubTask" -> subTasks.containsKey(id);
            default -> {
                System.out.println("Неизвестный тип задачи: " + type);
                yield false;
            }
        };
    }

    private void updateEpicStatus(int id) {
        Epic epic = getEpicById(id);
        if (epic == null) {
            System.out.println("Эпика с идентификатором " + id + " не существует");
            return;
        }
        ArrayList<Integer> subTaskIds = epic.getSubtasksIds();
        if (subTaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean haveDoneTasks = false;
        boolean haveInProgressTasks = false;
        boolean haveNewTasks = false;
        for (int subTaskId : subTaskIds) {
            TaskStatus taskStatus = subTasks.get(subTaskId).getStatus();
            if (taskStatus.equals(TaskStatus.DONE)) {
                haveDoneTasks = true;
            } else if (taskStatus.equals(TaskStatus.IN_PROGRESS)) {
                haveInProgressTasks = true;
            } else if (taskStatus.equals(TaskStatus.NEW)) {
                haveNewTasks = true;
            }
        }
        if (!haveDoneTasks && !haveInProgressTasks) {
            epic.setStatus(TaskStatus.NEW);
        } else if (haveDoneTasks && !haveInProgressTasks && !haveNewTasks) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
