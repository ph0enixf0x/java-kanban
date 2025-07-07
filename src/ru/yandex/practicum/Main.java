package ru.yandex.practicum;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        manager.createTask(new Task(manager.getTaskCounter(), "Первая таска",
                "Это первая тестовая таска", TaskStatus.NEW));
        manager.createEpic(new Epic(manager.getTaskCounter(), "Это первый эпик",
                "В этом эпике содержатся разные сабтаски", TaskStatus.NEW));
        manager.createSubTask(new SubTask(manager.getTaskCounter(), "Подтаска первого эпика",
                "Тестовая подтаска для первого эпика, не на что смотреть", TaskStatus.NEW, 2));

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubTasks();

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());
    }
}
