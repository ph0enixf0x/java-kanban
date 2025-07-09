package ru.yandex.practicum;

import ru.yandex.practicum.tasks.*;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        int taskId1 = manager.createTask(new Task("Первая задача",
                "Описание первой задачи"));
        int taskId2 = manager.createTask(new Task("Вторая задача",
                "Описание второй задачи"));
        int epicId1 = manager.createEpic(new Epic("Первый эпик",
                "Описание первого эпика"));
        int subTaskId1 = manager.createSubTask(new SubTask("Подзадача один",
                "Первая подзадача первого эпика", 3));
        int subTaskId2 = manager.createSubTask(new SubTask("Подзадача два",
                "Вторая подзадача первого эпика", 3));
        int epicId2 = manager.createEpic(new Epic("Второй эпик",
                "Описание второго эпика"));
        int subtaskId3 = manager.createSubTask(new SubTask("Подзадача один",
                "Единственная подздадача второго эпика", 6));

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());
    }
}
