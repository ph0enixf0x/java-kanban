package ru.yandex.practicum.tasks;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void checkIdenticalSubTasks() {
        SubTask subTask1 = new SubTask("Подзадача один", "Первая подзадача первого эпика",
                LocalDateTime.now(), Duration.ofMinutes(60), 1);
        SubTask subTask2 = new SubTask("Такая же подзадача", "У этой подзадачи такой же id",
                LocalDateTime.now(), Duration.ofMinutes(60), 1);
        assertEquals(subTask2, subTask1, "Подзадачи с одинаковым id должны быть идентичны");
    }
}