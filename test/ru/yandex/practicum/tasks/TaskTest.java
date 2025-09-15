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
    void getTaskAsString() {
        assertEquals("0,TASK,Тестовая задача,NEW,Описание тестовой задачи",
                Task.toString(new Task("Тестовая задача", "Описание тестовой задачи")),
                "Строка новой задачи не совпадает с ожидаемой");
    }

    @Test
    void getModifiedTaskAsString() {
        Task task = new Task("Тестовая задача[] 1", "Описание тестов$#$ой задачи");
        task.setId(4);
        task.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals("4,TASK,Тестовая задача[] 1,IN_PROGRESS,Описание тестов$#$ой задачи",
                Task.toString(task),
                "Строка измененной задачи не совпадает с ожидаемой");
    }

    @Test
    void getNewTaskFromString() {
        assertEquals(new Task("Тестовая задача", "Описание тестовой задачи"),
                Task.fromString("0,TASK,Тестовая задача,NEW,Описание тестовой задачи"),
                "Объект задачи, созданный из строки, не совпадает с ожидаемым");
    }

    @Test
    void getModifiedTaskIdFromString() {
        assertEquals(4,
                Task.fromString("4,TASK,Тестовая задача[] 1,IN_PROGRESS,Описание тестовой задачи").getId(),
                "Идентификатор модифицированной задачи не соответствует ожидаемому");
    }

    @Test
    void getModifiedTaskNameFromString() {
        assertEquals("Тестовая задача[] 1",
                Task.fromString("4,TASK,Тестовая задача[] 1,IN_PROGRESS,Описание тестовой задачи").getName(),
                "Название модифицированной задачи не соответствует ожидаемому");
    }

    @Test
    void getModifiedTaskStatusFromString() {
        assertEquals(TaskStatus.IN_PROGRESS,
                Task.fromString("4,TASK,Тестовая задача[] 1,IN_PROGRESS,Описание тестовой задачи")
                        .getStatus(),
                "Статус модифицированной задачи не соответствует ожидаемому");
    }

    @Test
    void getModifiedTaskDescriptionFromString() {
        assertEquals("Описание тестов$#$ой задачи",
                Task.fromString("4,TASK,Тестовая задача[] 1,IN_PROGRESS,Описание тестов$#$ой задачи")
                        .getDescription(),
                "Описание модифицированной задачи не соответствует ожидаемому");
    }

}