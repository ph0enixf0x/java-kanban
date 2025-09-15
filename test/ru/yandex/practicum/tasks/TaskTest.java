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

}