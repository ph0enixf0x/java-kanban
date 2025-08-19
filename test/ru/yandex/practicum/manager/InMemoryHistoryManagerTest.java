package ru.yandex.practicum.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager history;
    private static Task expectedTask1;
    private static Epic expectedEpic1;
    private static SubTask expectedSubTask1;

    @BeforeAll
    static void beforeAll() {
        expectedTask1 = new Task("Первая задача", "Описание первой задачи");
        expectedEpic1 = new Epic("Первый эпик", "Описание первого эпика");
        expectedEpic1.setId(1);
        expectedSubTask1 = new SubTask("Подзадача один",
                "Первая подзадача первого эпика", 1);
        expectedSubTask1.setId(2);
    }

    @BeforeEach
    void beforeEach() {
        history = new InMemoryHistoryManager();
    }

    @AfterEach
    void afterEach() {
        expectedTask1 = new Task("Первая задача", "Описание первой задачи");
        expectedEpic1 = new Epic("Первый эпик", "Описание первого эпика");
        expectedEpic1.setId(1);
        expectedSubTask1 = new SubTask("Подзадача один",
                "Первая подзадача первого эпика", 1);
        expectedSubTask1.setId(2);
    }

    @Test
    void getEmptyHistory() {
        assertEquals(0, history.getHistory().size(),
                "Полученная история не пустая");
    }

    @Test
    void getFilledHistory() {
        history.add(expectedTask1);
        history.add(expectedEpic1);
        history.add(expectedSubTask1);

        assertEquals(3, history.getHistory().size(),
                "Размер полученной истории не соответствует ожидаемому");
    }

    @Test
    void checkIfDuplicatesAreRemoved() {
        history.add(expectedTask1);
        history.add(expectedEpic1);
        history.add(expectedSubTask1);
        history.add(expectedEpic1);
        ArrayList<Task> testedHistory = history.getHistory();
        assertEquals(3, testedHistory.size(),
                "Размер полученной истории не соответствует ожидаемому");
        assertEquals(new ArrayList<>(List.of(expectedTask1, expectedSubTask1, expectedEpic1)), testedHistory,
                "Итоговый список истории не соответствует ожидаемому");
    }

    @Test
    void checkIfDuplicateTailRemoved() {
        history.add(expectedTask1);
        history.add(expectedEpic1);
        history.add(expectedSubTask1);
        history.add(expectedSubTask1);
        ArrayList<Task> testedHistory = history.getHistory();
        assertEquals(3, testedHistory.size(),
                "Размер полученной истории не соответствует ожидаемому");
        assertEquals(new ArrayList<>(List.of(expectedTask1, expectedEpic1, expectedSubTask1)), testedHistory,
                "Итоговый список истории не соответствует ожидаемому");
    }

    @Test
    void checkIfDuplicateHeadRemoved() {
        history.add(expectedTask1);
        history.add(expectedEpic1);
        history.add(expectedSubTask1);
        history.add(expectedTask1);
        ArrayList<Task> testedHistory = history.getHistory();
        assertEquals(3, testedHistory.size(),
                "Размер полученной истории не соответствует ожидаемому");
        assertEquals(new ArrayList<>(List.of(expectedEpic1, expectedSubTask1, expectedTask1)), testedHistory,
                "Итоговый список истории не соответствует ожидаемому");
    }

    @Test
    void removeTaskFromHistory() {
        history.add(expectedTask1);
        history.add(expectedEpic1);
        history.add(expectedSubTask1);
        history.remove(expectedTask1.getId());
        assertFalse(history.getHistory().contains(expectedTask1),
                "Ожидаемая задача не была удалена из истории");

    }

    @Test
    void checkEmptyHistoryAfterDeletion() {
        history.add(expectedTask1);
        history.add(expectedEpic1);
        history.add(expectedSubTask1);
        history.remove(expectedTask1.getId());
        history.remove(expectedEpic1.getId());
        history.remove(expectedSubTask1.getId());

        assertTrue(history.getHistory().isEmpty(),
                "Ожидался пустой список истории");
    }
}