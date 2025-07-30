package ru.yandex.practicum.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager manager;
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
        manager = new InMemoryTaskManager();
    }

    @AfterEach
    void afterEach() {
        expectedTask1 = new Task("Первая задача", "Описание первой задачи");
        expectedEpic1 = new Epic("Первый эпик", "Описание первого эпика");
        expectedSubTask1 = new SubTask("Подзадача один",
                "Первая подзадача первого эпика", 1);
    }

    @Test
    void createTaskAndGetTaskById() {
        int taskId = manager.createTask(expectedTask1);
        Task resultTask = manager.getTaskById(taskId);
        assertNotNull(resultTask, "Задача не найдена");
        assertEquals(expectedTask1, resultTask,
                "Возвращенная задача несоответствует ожидаемой");
    }

    @Test
    void createEpicAndGetEpicById() {
        int epicId = manager.createEpic(expectedEpic1);
        Epic resultEpic = manager.getEpicById(epicId);
        assertNotNull(resultEpic, "Эпик не найден");
        assertEquals(expectedEpic1, resultEpic,
                "Возвращенный эпик несоответствует ожидаемому");
    }

    @Test
    void createSubtaskAndGetSubtaskById() {
        manager.createEpic(expectedEpic1);
        int subTaskId = manager.createSubTask(expectedSubTask1);
        SubTask resultSubtask = manager.getSubTaskById(subTaskId);
        assertNotNull(resultSubtask, "Подзадача не найдена");
        assertEquals(expectedSubTask1, resultSubtask,
                "Возвращенная подзадача несоответствует ожидаемой");
    }

    @Test
    void checkAddingSubtaskAsSelfEpicRestriction() {
        int subTaskId = manager.createSubTask(expectedSubTask1);
        assertEquals(0, subTaskId,
                "Должно быть нельзя сделать подзадачу своим же эпиком");
    }

    @Test
    void checkAddedTaskFields() {
        Task task = manager.getTaskById(manager.createTask(expectedTask1));
        assertEquals(1, task.getId(), "Неожиданный идентификатор задачи");
        assertEquals("Первая задача", task.getName(), "Неожиданное имя задачи");
        assertEquals("Описание первой задачи", task.getDescription(), "Неожиданное описание задачи");
    }
}