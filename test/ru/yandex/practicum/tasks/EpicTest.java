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
}