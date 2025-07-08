package ru.yandex.practicum;

import ru.yandex.practicum.tasks.*;

import java.util.ArrayList;
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
        if(isExistingTask("Task", taskId)) {
            System.out.println("Такая задача уже существует");
            return;
        }
        tasks.put(taskId, task);
        increaseTaskCounter();
    }

    public void createEpic(Epic epic) {
        int epicId = epic.getId();
        if(isExistingTask("Epic", epicId)) {
            System.out.println("Такой эпик уже существует");
            return;
        }
        epics.put(epicId, epic);
        increaseTaskCounter();
    }

    public void createSubTask(SubTask subTask) {
        int subTaskId = subTask.getId();
        int epicId = subTask.getEpicId();
        if(isExistingTask("SubTask", subTaskId)) {
            System.out.println("Такая подзадача уже существует");
            return;
        } else if(!isExistingTask("Epic", epicId)) {
            System.out.println("Указанного в подзадаче эпика не существует");
            return;
        }
        subTasks.put(subTaskId, subTask);
        increaseTaskCounter();
        epics.get(epicId).addSubTask(subTaskId);
        updateEpicStatus(epicId);
    }

    public void updateTask(Task task) {
        int taskId = task.getId();
        if(!isExistingTask("Task", taskId)) {
            System.out.println("Несуществующий идентификатор задачи");
            return;
        }
        tasks.put(taskId, task);
    }

    public void updateEpic(Epic epic) {
        int epicId = epic.getId();
        if(!isExistingTask("Epic", epicId)) {
            System.out.println("Несуществующий идентификатор эпика");
            return;
        }
        epics.put(epicId, epic);
        updateEpicStatus(epicId);
    }

    public void updateSubTask(SubTask subTask) {
        int subTaskId = subTask.getId();
        if(!isExistingTask("SubTask", subTaskId)) {
            System.out.println("Несуществующий идентификатор подзадачи");
            return;
        }
        subTasks.put(subTaskId, subTask);
        updateEpicStatus(subTask.getEpicId());
    }

    public void deleteTaskById(int id) {
        if(getTaskById(id) != null) tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = getEpicById(id);
        if(epic != null) {
            for (int subtaskId : epic.getSubtasksIds()) {
                subTasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    public void deleteSubTaskById(int subTaskId) {
        if(getSubTaskById(subTaskId) == null) {
            System.out.println("Подзадачи с идентификатором " + subTaskId + " несуществует.");
        }
        int epicId = subTasks.get(subTaskId).getEpicId();
        epics.get(epicId).removeSubTask(subTaskId);
        updateEpicStatus(epicId);
        subTasks.remove(subTaskId);
    }

    public ArrayList<SubTask> getEpicSubTasks(int id) {
        Epic epic = getEpicById(id);
        ArrayList<SubTask> result = new ArrayList<>();
        if(epic != null) {
            ArrayList<Integer> subtaskIds = epic.getSubtasksIds();
            for (int subtaskId : subtaskIds) {
                result.add(subTasks.get(subtaskId));
            }
            return result;
        }
        System.out.println("Такого эпика не существует");
        return null;
    }

    private void updateEpicStatus(int id) {
        Epic epic = getEpicById(id);
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
