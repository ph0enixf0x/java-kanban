package ru.yandex.practicum.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void checkIdenticalTasks() {
        Task task1 = new Task("Первая задача", "Описание первой задачи");
        Task task2 = new Task("Такая же задача", "У этой задачи такой же id");
        assertEquals(task2, task1, "Задачи с одинаковыми id должны быть идентичны");
    }

    @Test
    void checkIdenticalEpics() {
        Epic epic1 = new Epic("Первый эпик", "Описание первого эпика");
        Epic epic2 = new Epic("Такой же эпик", "У этого эпика такой же id");
        assertEquals(epic2, epic1, "Эпики с одинаковыми id должны быть идентичны");
    }

    @Test
    void checkIdenticalSubTasks() {
        SubTask subTask1 = new SubTask("Подзадача один", "Первая подзадача первого эпика", 1);
        SubTask subTask2 = new SubTask("Такая же подзадача", "У этой подзадачи такой же id", 1);
        assertEquals(subTask2, subTask1, "Подзадачи с одинаковым id должны быть идентичны");
    }

    @Test
    void checkAddingEpicAsSelfSubtaskRestriction() {
        Epic epic1 = new Epic("Первый эпик", "Описание первого эпика");
        epic1.addSubTask(epic1.getId());
        assertEquals(0, epic1.getSubtasksIds().size(),
                "Должно быть нельзя добавить эпик как подзадачу самому себе");
    }
}