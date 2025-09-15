package ru.yandex.practicum.tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIds;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksIds = new ArrayList<>();
        this.type = TaskType.EPIC;
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void addSubTask(int subtaskId) {
        if (subtaskId == id) {
            System.out.println("Нельзя добавить эпик как подзадачу самому себе!");
            return;
        }
        subtasksIds.add(subtaskId);
    }

    public void removeSubTask(Integer subtaskId) {
        subtasksIds.remove(subtaskId);
    }

    public void clearSubTasks() {
        subtasksIds.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasks=" + subtasksIds +
                '}';
    }

    public static String toString(Epic task) {
        return String.join(",",
                String.valueOf(task.id),
                String.valueOf(task.type),
                task.name,
                String.valueOf(task.status),
                task.description,
                task.subtasksIds.toString().replace("[", "").replace("]", "")
                        .replaceAll("\\s+", ""));
    }

    public static Epic fromString(String stringedTask) {
        String[] separatedTask = stringedTask.split(",");
        Epic task = new Epic(separatedTask[2], separatedTask[4]);
        task.setId(Integer.parseInt(separatedTask[0]));
        task.setStatus(TaskStatus.valueOf(separatedTask[3].toUpperCase()));
        if (separatedTask.length > 5) {
            for (int i = 5; i < separatedTask.length; i++) {
               task.addSubTask(Integer.parseInt(separatedTask[i]));
            }
        }
        return task;
    }
}
