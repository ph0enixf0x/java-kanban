package ru.yandex.practicum.manager;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        super.beforeEach(testInfo);
        manager = new InMemoryTaskManager();
    }

    @Test
    void getHistoryManagerIntegration() {
        int epicId = manager.createEpic(expectedEpic);
        int taskId = manager.createTask(expectedTask);
        int subTaskId = manager.createSubTask(expectedSubTask);

        manager.getEpicById(epicId);
        manager.getTaskById(taskId);
        manager.getSubTaskById(subTaskId);

        assertEquals(3, manager.getHistory().size(),
                "Размер полученной истории не соответствует ожидаемому");
    }
}