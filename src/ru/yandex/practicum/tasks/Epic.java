package ru.yandex.practicum.tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIds;

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        this.subtasksIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void addSubTask(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasksIds +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
