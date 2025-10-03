package ru.yandex.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIds;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, null, Duration.ZERO);
        this.subtasksIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public LocalDateTime getEndTime() { return endTime; }

    public void addSubTask(int subtaskId) {
        if (subtaskId == id) {
            System.out.println("Нельзя добавить эпик как подзадачу самому себе!");
            return;
        }
        subtasksIds.add(subtaskId);
    }

    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

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
