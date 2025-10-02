package ru.yandex.practicum.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void checkLoadFromEmptyFile() throws IOException {
        File testFile = File.createTempFile("test", ".txt");
        assertNotNull(FileBackedTaskManager.loadFromFile(testFile),
                    "Не удалось загрузить файловый менеджер из пустого файла");
    }

    @Test
    void checkLoadFromEmptyFileWithHeader() throws IOException {
            File testFile = File.createTempFile("test", ".txt");
            try (FileWriter fr = new FileWriter(testFile)) {
                String header = "id,type,name,status,description,epic\n";
                fr.write(header);
            }
            assertNotNull(FileBackedTaskManager.loadFromFile(testFile),
                    "Не удалось загрузить файловый менеджер из пустого файла");
    }

    @Test
    void checkRestoreFromSaveFile() throws IOException {
        File testFile = File.createTempFile("test", ".txt");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);
        int taskId = manager.createTask(new Task("Первая задача", "Описание первой задачи",
                LocalDateTime.now(), Duration.ofMinutes(60)));
        int epicId = manager.createEpic(new Epic("Первый эпик", "Описание первого эпика"));
        int subTaskId = manager.createSubTask(new SubTask("Первая подзадача", "Подзадача первого эпика",
                LocalDateTime.now(), Duration.ofMinutes(60), epicId));
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

        assertEquals(2, epic.getId(),
                "Идентификатор эпика не соответствует ожидаемому");
        assertEquals("Первый эпик", epic.getName(),
                "Название эпика не соответствует ожидаемому");
        assertEquals("Описание первого эпика", epic.getDescription(),
                "Описание полученного эпика не соответствует ожидаемому");
        assertEquals(TaskStatus.DONE, epic.getStatus(),
                "Статус эпика не соответствует ожидаемому");
        assertEquals(List.of(3), epic.getSubtasksIds(),
                "Список подзадач эпика не соответствует ожидаемому");

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

        assertEquals(4, manager2.taskCounter,
                "Номер следующей задачи в загруженном менеджере не соответствует ожидаемому");
    }
}