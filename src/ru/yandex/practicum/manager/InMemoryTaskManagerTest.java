package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager manager;
    private final Task expectedTask1 = new Task("Первая задача", "Описание первой задачи");
    private final Epic expectedEpic1 = new Epic("Первый эпик", "Описание первого эпика");
    private final SubTask expectedSubTask1 = new SubTask("Подзадача один",
            "Первая подзадача первого эпика", 1);

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void getTaskById() {
        int taskId = manager.createTask(expectedTask1);
        expectedTask1.setId(1);
        assertEquals(expectedTask1, manager.getTaskById(taskId),
                "Возвращенная задача несоответствует ожидаемой");
    }

    @Test
    void getEpicById() {
        int epicId = manager.createEpic(expectedEpic1);
        expectedEpic1.setId(1);
        assertEquals(expectedEpic1, manager.getEpicById(epicId),
                "Возвращенный эпик несоответствует ожидаемому");
    }

    @Test
    void getSubTaskById() {
        manager.createEpic(expectedEpic1);
        int subTaskId = manager.createSubTask(expectedSubTask1);
        expectedSubTask1.setId(2);
        assertEquals(expectedSubTask1, manager.getSubTaskById(subTaskId),
                "Возвращенная подзадача несоответствует ожидаемой");
    }

    @Test
    void getEmptyHistory() {
        assertEquals(0, manager.getHistory().size(),
                "Полученная история не пустая");
    }

    @Test
    void getFilledHistory() {
        int epicId = manager.createEpic(expectedEpic1);
        int subTaskId = manager.createSubTask(expectedSubTask1);
        int taskId = manager.createTask(expectedTask1);

        manager.getEpicById(epicId);
        manager.deleteSubTaskById(subTaskId);
        manager.getTaskById(taskId);

        System.out.println(manager.getHistory());

        assertEquals(3, manager.getHistory().size(),
                "Размер полученной истории не соответствует ожидаемому");
    }

    @Test
    void getOverfilledHistory() {
        int epicId = manager.createEpic(expectedEpic1);
        int subTaskId = manager.createSubTask(expectedSubTask1);
        int taskId = manager.createTask(expectedTask1);

        for (int i = 0; i < 4; i++) {
            manager.getEpicById(epicId);
            manager.deleteSubTaskById(subTaskId);
            manager.getTaskById(taskId);
        }

        assertEquals(10, manager.getHistory().size());
    }
}