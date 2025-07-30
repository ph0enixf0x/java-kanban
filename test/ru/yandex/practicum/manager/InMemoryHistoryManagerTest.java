package ru.yandex.practicum.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;

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
        expectedSubTask1 = new SubTask("Подзадача один",
                "Первая подзадача первого эпика", 1);
    }

    @BeforeEach
    void beforeEach() {
        history = new InMemoryHistoryManager();
    }

    @AfterEach
    void afterEach() {
        expectedTask1 = new Task("Первая задача", "Описание первой задачи");
        expectedEpic1 = new Epic("Первый эпик", "Описание первого эпика");
        expectedSubTask1 = new SubTask("Подзадача один",
                "Первая подзадача первого эпика", 1);
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
    void getOverfilledHistory() {
        for (int i = 0; i < 4; i++) {
            history.add(expectedTask1);
            history.add(expectedEpic1);
            history.add(expectedSubTask1);
        }

        ArrayList<Task> resultHistory = history.getHistory();

        assertEquals(10, resultHistory.size(),
                "Размер истории не равен ожидаемому");
        assertEquals(expectedSubTask1, resultHistory.getFirst(),
                "На первом месте неожиданная задача");
        assertEquals(expectedSubTask1, resultHistory.getLast(),
                "На последнем месте неожиданная задача");
    }
}