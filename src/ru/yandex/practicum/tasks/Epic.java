package ru.yandex.practicum.tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIds;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.subtasksIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void addSubTask(int subtaskId) {
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
}
