package ru.yandex.practicum.tasks;

import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected TaskType type;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.type = TaskType.TASK;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskType getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public static String toString(Task task) {
        return String.join(",",
                String.valueOf(task.id),
                String.valueOf(task.type),
                task.name,
                String.valueOf(task.status),
                task.description);
    }

    public static Task fromString(String stringedTask) {
        String[] separatedTask = stringedTask.split(",");
        Task task = new Task(separatedTask[2], separatedTask[4]);
        task.setId(Integer.parseInt(separatedTask[0]));
        task.setStatus(TaskStatus.valueOf(separatedTask[3].toUpperCase()));
        return task;
    }
}
