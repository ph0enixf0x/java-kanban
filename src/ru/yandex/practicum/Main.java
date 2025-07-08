package ru.yandex.practicum;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        System.out.println("-".repeat(5) + " Создаем первые таски");

        manager.createTask(new Task(manager.getTaskCounter(), "Первая таска",
                "Это первая тестовая таска"));
        manager.createEpic(new Epic(manager.getTaskCounter(), "Это первый эпик",
                "В этом эпике содержатся разные сабтаски"));
        manager.createSubTask(new SubTask(manager.getTaskCounter(), "Подтаска первого эпика",
                "Тестовая подтаска для первого эпика, не на что смотреть",
                (Integer) manager.getEpics().keySet().toArray()[0]));

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        System.out.println("-".repeat(5) + " Удаляем все таски");

        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubTasks();

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        System.out.println("-".repeat(5) + " Создаем дополнительные таски");

        manager.createTask(new Task(manager.getTaskCounter(), "Вторая таска",
                "Это вторая тестовая таска"));
        manager.createTask(new Task(manager.getTaskCounter(), "третья таска",
                "Это третья тестовая таска"));
        manager.createEpic(new Epic(manager.getTaskCounter(), "Это второй эпик",
                "В этом эпике содержатся разные сабтаски"));
        manager.createSubTask(new SubTask(manager.getTaskCounter(), "Подтаска второго эпика",
                "Тестовая подтаска для второго эпика, не на что смотреть",
                (Integer) manager.getEpics().keySet().toArray()[0]));
        manager.createSubTask(new SubTask(manager.getTaskCounter(), "Подтаска второго эпика",
                "Тестовая подтаска для второго эпика, не на что смотреть",
                (Integer) manager.getEpics().keySet().toArray()[0]));
        manager.createSubTask(new SubTask(manager.getTaskCounter(), "Подтаска второго эпика",
                "Тестовая подтаска для второго эпика, не на что смотреть",
                (Integer) manager.getEpics().keySet().toArray()[0]));
        manager.createSubTask(new SubTask(manager.getTaskCounter(), "Подтаска второго эпика",
                "Тестовая подтаска для второго эпика, не на что смотреть",
                0));
        manager.createEpic(new Epic(manager.getTaskCounter(), "Это третий эпик",
                "В этом эпике содержатся разные сабтаски"));

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        System.out.println("-".repeat(5) + " Получаем таски по идентификаторам");

        System.out.println(manager.getTaskById(4));
        System.out.println(manager.getTaskById(5));
        System.out.println(manager.getEpicById(6));
        System.out.println(manager.getEpicById(7));
        System.out.println(manager.getSubTaskById(7));
        System.out.println(manager.getSubTaskById(9));
        System.out.println(manager.getSubTaskById(10));

        System.out.println("-".repeat(5) + " Обновляем таски");

        Task updatedTask = manager.getTaskById(4);
        updatedTask.setName("Это новое название для таски");
        updatedTask.setDescription("А это новое описание того о чем таска");
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(updatedTask);

        manager.updateTask(new Task(6, "Несуществующая таска",
                "Эта таска не должна попасть в список тасок"));

        Epic updatedEpic = manager.getEpicById(6);
        updatedEpic.setName("Второй эпик");
        updatedEpic.setDescription("Новое описание для второго эпика");
        manager.updateEpic(updatedEpic);

        SubTask updatedSubTask = manager.getSubTaskById(7);
        updatedSubTask.setName("Первая сабтаска второго эпика");
        updatedSubTask.setDescription("Новое описание сабтаски второго эпика");
        updatedSubTask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(updatedSubTask);

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        System.out.println("-".repeat(5) + " Удаляем таски по идентификаторам");

        manager.deleteTaskById(5);
        manager.deleteEpicById(10);
        manager.deleteSubTaskById(9);

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        System.out.println("-".repeat(5) + " Получаем все таски определенного эпика");

        System.out.println(manager.getEpicSubTasks(6));

        System.out.println("-".repeat(5) + " Удаляем эпик с сабтасками");

        manager.deleteEpicById(6);
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        System.out.println("-".repeat(5) + " Проверяем изменение статусов эпика");
        manager.createEpic(new Epic(manager.getTaskCounter(), "Статусный эпик",
                "Это эпик для тестирования смены статусов"));
        manager.createSubTask(new SubTask(manager.getTaskCounter(), "Подтаска 1",
                "Эта подтаска 1",
                11));
        manager.createSubTask(new SubTask(manager.getTaskCounter(), "Подтаска 2",
                "Эта подтаска 2",
                11));
        manager.createSubTask(new SubTask(manager.getTaskCounter(), "Подтаска 3",
                "Эта подтаска 3",
                11));
        System.out.println(manager.getEpics());
        updatedSubTask = manager.getSubTaskById(12);
        updatedSubTask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(updatedSubTask);
        System.out.println(manager.getEpics());
        updatedSubTask.setStatus(TaskStatus.NEW);
        manager.updateSubTask(updatedSubTask);
        System.out.println(manager.getEpics());
        SubTask updatedSubTask2 = manager.getSubTaskById(13);
        SubTask updatedSubTask3 = manager.getSubTaskById(14);
        updatedSubTask.setStatus(TaskStatus.DONE);
        updatedSubTask2.setStatus(TaskStatus.DONE);
        updatedSubTask3.setStatus(TaskStatus.DONE);
        manager.updateSubTask(updatedSubTask);
        manager.updateSubTask(updatedSubTask2);
        manager.updateSubTask(updatedSubTask3);
        System.out.println(manager.getEpics());
    }
}
