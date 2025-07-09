package ru.yandex.practicum;

import ru.yandex.practicum.tasks.*;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        System.out.println("-".repeat(5) + " Тест 1: Создание задач");
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
        int subTaskId3 = manager.createSubTask(new SubTask("Подзадача три",
                "Единственная подздадача второго эпика", 6));

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        System.out.println("-".repeat(5) + " Тест 2: Обновление задач");
        Task task1 = manager.getTaskById(taskId1);
        Task task2 = manager.getTaskById(taskId2);
        Epic epic1 = manager.getEpicById(epicId1);
        Epic epic2 = manager.getEpicById(epicId2);
        SubTask subTask1 = manager.getSubTaskById(subTaskId1);
        SubTask subTask2 = manager.getSubTaskById(subTaskId2);
        SubTask subTask3 = manager.getSubTaskById(subTaskId3);

        task1.setStatus(TaskStatus.DONE);
        task2.setDescription("Описание этой задачи было изменено");
        epic1.setName("Новое название первого эпика");
        epic2.setDescription("Новое описание второго эпика");
        subTask1.setDescription("Уточненное описание первой подзадачи");
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        subTask3.setStatus(TaskStatus.DONE);

        manager.updateTask(task1);
        manager.updateTask(task2);
        manager.updateEpic(epic1);
        manager.updateEpic(epic2);
        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        System.out.println("-".repeat(5) + " Тест 3: Удаление задач");
        manager.deleteTaskById(taskId2);
        manager.deleteSubTaskById(subTaskId2);
        manager.deleteEpicById(epicId2);

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());
    }
}
