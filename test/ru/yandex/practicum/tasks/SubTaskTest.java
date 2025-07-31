package ru.yandex.practicum.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void checkIdenticalSubTasks() {
        SubTask subTask1 = new SubTask("Подзадача один", "Первая подзадача первого эпика", 1);
        SubTask subTask2 = new SubTask("Такая же подзадача", "У этой подзадачи такой же id", 1);
        assertEquals(subTask2, subTask1, "Подзадачи с одинаковым id должны быть идентичны");
    }
}