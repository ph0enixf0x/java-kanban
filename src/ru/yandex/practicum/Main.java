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
                "Тестовая подтаска для первого эпика, не на что смотреть", TaskStatus.NEW, (Integer) manager.getEpics().keySet().toArray()[0])); // TODO: переделать получение id

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubTasks();

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        manager.createTask(new Task(manager.getTaskCounter(), "Вторая таска",
                "Это вторая тестовая таска", TaskStatus.NEW));
        manager.createTask(new Task(manager.getTaskCounter(), "третья таска",
                "Это третья тестовая таска", TaskStatus.NEW));
        manager.createEpic(new Epic(manager.getTaskCounter(), "Это второй эпик",
                "В этом эпике содержатся разные сабтаски", TaskStatus.NEW));
        manager.createSubTask(new SubTask(manager.getTaskCounter(), "Подтаска второго эпика",
                "Тестовая подтаска для второго эпика, не на что смотреть", TaskStatus.NEW, (Integer) manager.getEpics().keySet().toArray()[0]));
        manager.createSubTask(new SubTask(manager.getTaskCounter(), "Подтаска второго эпика",
                "Тестовая подтаска для второго эпика, не на что смотреть", TaskStatus.NEW, (Integer) manager.getEpics().keySet().toArray()[0]));
        manager.createSubTask(new SubTask(manager.getTaskCounter(), "Подтаска второго эпика",
                "Тестовая подтаска для второго эпика, не на что смотреть", TaskStatus.NEW, (Integer) manager.getEpics().keySet().toArray()[0]));

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        System.out.println(manager.getTaskById(4));
        System.out.println(manager.getTaskById(5));
        System.out.println(manager.getEpicById(6));
        System.out.println(manager.getEpicById(7));
        System.out.println(manager.getSubTaskById(7));
        System.out.println(manager.getSubTaskById(9));
        System.out.println(manager.getSubTaskById(10));
    }
}
