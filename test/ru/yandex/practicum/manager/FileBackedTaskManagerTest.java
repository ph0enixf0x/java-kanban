package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import ru.yandex.practicum.tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File testFile;

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        super.beforeEach(testInfo);
        try {
            testFile = File.createTempFile("test", ".txt");
            manager = new FileBackedTaskManager(testFile);
        } catch (IOException e) {
            System.out.println("Возникла ошибка при создании тестового файла! Тест прерван.");
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    void checkLoadFromEmptyFile() {
        assertNotNull(FileBackedTaskManager.loadFromFile(testFile),
                    "Не удалось загрузить файловый менеджер из пустого файла");
    }

    @Test
    void checkLoadFromEmptyFileWithHeader() throws IOException {
            File testFile = File.createTempFile("testHeader", ".txt");
            try (FileWriter fr = new FileWriter(testFile)) {
                String header = "id,type,name,status,description,start,duration,epic\n";
                fr.write(header);
            }
            assertNotNull(FileBackedTaskManager.loadFromFile(testFile),
                    "Не удалось загрузить файловый менеджер из пустого файла с шапкой");
    }

    @Test
    void checkRestoreFromSaveFile() {
        LocalDateTime testTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        int taskId = manager.createTask(new Task("Первая задача", "Описание первой задачи",
                testTime, Duration.ofMinutes(60)));
        int epicId = manager.createEpic(new Epic("Первый эпик", "Описание первого эпика"));
        int subTaskId = manager.createSubTask(new SubTask("Первая подзадача", "Подзадача первого эпика",
                testTime.plusDays(1), Duration.ofMinutes(60), epicId));
        manager.createSubTask(new SubTask("Вторая подзадача", "Подзадача первого эпика",
                testTime.plusDays(2), Duration.ofMinutes(60), epicId));
        SubTask testSubTask = manager.getSubTaskById(subTaskId);
        testSubTask.setStatus(TaskStatus.DONE);
        manager.updateSubTask(testSubTask);
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(manager.getSaveFile());
        Task task = manager2.getTaskById(taskId);
        Epic epic = manager2.getEpicById(epicId);
        SubTask subTask = manager2.getSubTaskById(subTaskId);

        assertEquals(1, task.getId(),
                "Идентификатор задачи не соответствует ожидаемому");
        assertEquals("Первая задача", task.getName(),
                "Название полученное задачи не соответствует ожидаемому");
        assertEquals("Описание первой задачи", task.getDescription(),
                "Описание задачи не соответствует ожидаемому");
        assertEquals(TaskStatus.NEW, task.getStatus(),
                "Статус задачи не соответствует ожидаемому");
        assertEquals(testTime, task.getStartTime(),
                "Время начала выполнения задачи не соответствует ожидаемому");
        assertEquals(Duration.ofMinutes(60), task.getDuration(),
                "Длительность выполнения задачи не соответствует ожидаемому");

        assertEquals(2, epic.getId(),
                "Идентификатор эпика не соответствует ожидаемому");
        assertEquals("Первый эпик", epic.getName(),
                "Название эпика не соответствует ожидаемому");
        assertEquals("Описание первого эпика", epic.getDescription(),
                "Описание полученного эпика не соответствует ожидаемому");
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Статус эпика не соответствует ожидаемому");
        assertEquals(List.of(3, 4), epic.getSubtasksIds(),
                "Список подзадач эпика не соответствует ожидаемому");
        assertEquals(testTime.plusDays(1), epic.getStartTime(),
                "Время начала выполнения эпика не соответствует ожидаемому");
        assertEquals(Duration.ofMinutes(120), epic.getDuration(),
                "Длительность выполнения эпика не соответствует ожидаемому");
        assertEquals(testTime.plusDays(2).plusMinutes(60), epic.getEndTime(),
                "Время окончания выполнения эпика не соответствует ожидаемому");

        assertEquals(3, subTask.getId(),
                "Идентификатор подзадачи не соответствует ожидаемому");
        assertEquals("Первая подзадача", subTask.getName(),
                "Название подзадачи не соответствует ожидаемому");
        assertEquals("Подзадача первого эпика", subTask.getDescription(),
                "Описание подзадачи не соответствует ожидаемому");
        assertEquals(TaskStatus.DONE, subTask.getStatus(),
                "Статус подзадачи не соответствует ожидаемому");
        assertEquals(epicId, subTask.getEpicId(),
                "Номер эпика полученной подзадачи не соответствует ожидаемому");
        assertEquals(testTime.plusDays(1), subTask.getStartTime(),
                "Время начала выполнения подзадачи не соответствует ожидаемому");
        assertEquals(Duration.ofMinutes(60), subTask.getDuration(),
                "Длительность выполнения подзадачи не соответствует ожидаемому");

        assertEquals(5, manager2.taskCounter,
                "Номер следующей задачи в загруженном менеджере не соответствует ожидаемому");
        assertEquals(manager.getPrioritizedTasks(), manager2.getPrioritizedTasks(),
                "Приоритезированный список задач не соответствует ожидаемому");
    }
}