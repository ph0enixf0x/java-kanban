import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.InMemoryTaskManager;
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
}