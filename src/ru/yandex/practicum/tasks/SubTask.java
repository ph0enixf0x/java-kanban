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

    public static SubTask fromString(String stringedTask) {
        String[] separatedTask = stringedTask.split(",");
        SubTask task = new SubTask(separatedTask[2], separatedTask[4], Integer.parseInt(separatedTask[5]));
        task.setId(Integer.parseInt(separatedTask[0]));
        task.setStatus(TaskStatus.valueOf(separatedTask[3].toUpperCase()));
        return task;
    }
}
