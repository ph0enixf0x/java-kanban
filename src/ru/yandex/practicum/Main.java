package ru.yandex.practicum;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        manager.createTask(new Task(manager.getTaskCounter(), "Первая задача",
                "Описание первой задачи"));
        manager.createTask(new Task(manager.getTaskCounter(), "Вторая задача",
                "Описание второй задачи"));
        manager.createEpic(new Epic(manager.getTaskCounter(), "Первый эпик",
                "Описание первого эпика"));
        manager.createSubTask(new SubTask(manager.getTaskCounter(), "Подзадача один",
                "Первая подзадача первого эпика", 3));
        manager.createSubTask(new SubTask(manager.getTaskCounter(), "Подзадача два",
                "Вторая подзадача первого эпика", 3));
        manager.createEpic(new Epic(manager.getTaskCounter(), "Второй эпик",
                "Описание второго эпика"));
        manager.createSubTask(new SubTask(manager.getTaskCounter(), "Подзадача один",
                "Единственная подздадача второго эпика", 6));

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        Task task1 = manager.getTaskById(1);
        task1.setStatus(TaskStatus.DONE);
        manager.updateTask(task1);

        Task task2 = manager.getTaskById(2);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task2);

        SubTask subTask1 = manager.getSubTaskById(4);
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(subTask1);

        SubTask subTask2 = manager.getSubTaskById(7);
        subTask2.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subTask2);

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        manager.deleteTaskById(2);
        manager.deleteEpicById(6);
        manager.deleteSubTaskById(4);

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());
    }
}
