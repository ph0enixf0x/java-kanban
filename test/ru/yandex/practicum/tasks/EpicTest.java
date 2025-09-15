package ru.yandex.practicum.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void checkIdenticalEpics() {
        Epic epic1 = new Epic("Первый эпик", "Описание первого эпика");
        Epic epic2 = new Epic("Такой же эпик", "У этого эпика такой же id");
        assertEquals(epic2, epic1, "Эпики с одинаковыми id должны быть идентичны");
    }

    @Test
    void checkAddingEpicAsSelfSubtaskRestriction() {
        Epic epic1 = new Epic("Первый эпик", "Описание первого эпика");
        epic1.addSubTask(epic1.getId());
        assertEquals(0, epic1.getSubtasksIds().size(),
                "Должно быть нельзя добавить эпик как подзадачу самому себе");
    }

    @Test
    void getEpicAsString() {
        assertEquals("0,EPIC,Тестовый эпик,NEW,Описание тестового эпика,",
                Epic.toString(new Epic("Тестовый эпик", "Описание тестового эпика")),
                "Строка новой подзадачи не совпадает с ожидаемой");
    }

    @Test
    void getModifiedEpicAsString() {
        Epic task = new Epic("Тестовый эпик[] 1", "Описание тест&&^%ового эпика");
        task.setId(4);
        task.addSubTask(1);
        task.addSubTask(5);
        task.setStatus(TaskStatus.DONE);
        assertEquals("4,EPIC,Тестовый эпик[] 1,DONE,Описание тест&&^%ового эпика,1,5",
                Epic.toString(task),
                "Строка измененной подзадачи не совпадает с ожидаемой");
    }
}