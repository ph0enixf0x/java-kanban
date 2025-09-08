package ru.yandex.practicum.tasks;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicId=" + epicId +
                '}';
    }

    public static String toString(SubTask task) {
        return String.join(",",
                String.valueOf(task.id),
                String.valueOf(task.type),
                task.name,
                String.valueOf(task.status),
                task.description,
                String.valueOf(task.getEpicId()));
    }
}
