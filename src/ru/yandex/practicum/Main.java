package ru.yandex.practicum;

import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.*;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = new Managers().getDefault();

        int taskId1 = manager.createTask(new Task("Первая задача", "Описание первой задачи"));
        int taskId2 = manager.createTask(new Task("Вторая задача", "Описание второй задачи"));
        int epicId1 = manager.createEpic(new Epic("Первый эпик", "Описание первого эпика"));
        int subTaskId1 = manager.createSubTask(new SubTask("Подзадача один",
                "Первая подзадача первого эпика", epicId1));
        int subTaskId2 = manager.createSubTask(new SubTask("Подзадача два",
                "Вторая подзадача первого эпика", epicId1));
        int subTaskId3 = manager.createSubTask(new SubTask("Подзадача три",
                "Третья подзадача первого эпика", epicId1));
        int epicId2 = manager.createEpic(new Epic("Второй эпик", "Описание второго эпика"));

        manager.getTaskById(taskId1);
        System.out.println(manager.getHistory());
        manager.getTaskById(taskId2);
        System.out.println(manager.getHistory());
        manager.getTaskById(taskId1);
        System.out.println(manager.getHistory());
        manager.getEpicById(epicId1);
        System.out.println(manager.getHistory());
        manager.getEpicById(epicId2);
        System.out.println(manager.getHistory());
        manager.getSubTaskById(subTaskId1);
        System.out.println(manager.getHistory());
        manager.getEpicById(epicId2);
        System.out.println(manager.getHistory());
        manager.getSubTaskById(subTaskId2);
        System.out.println(manager.getHistory());
        manager.getSubTaskById(subTaskId3);
        System.out.println(manager.getHistory());
        manager.getSubTaskById(subTaskId1);
        System.out.println(manager.getHistory());
        manager.getEpicById(epicId1);
        System.out.println(manager.getHistory());

        manager.deleteTaskById(taskId2);
        System.out.println(manager.getHistory());
        manager.deleteEpicById(epicId1);
        System.out.println(manager.getHistory());

    }
}
