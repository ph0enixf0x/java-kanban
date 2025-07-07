package ru.yandex.practicum.tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<SubTask> subtasks;

    public Epic(int id, String name, String description, TaskStatus status, ArrayList<SubTask> subtasks) {
        super(id, name, description, status);
        this.subtasks = subtasks;
    }

    public ArrayList<SubTask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<SubTask> subtasks) {
        this.subtasks = subtasks;
    }
}
