package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;

    @BeforeEach
    void beforeEach() {
        try {
            File testFile = File.createTempFile("test", ".txt");
            manager = new FileBackedTaskManager(testFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

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
    void checkLoadFromNormalFile() throws IOException {
        File testFile = File.createTempFile("test", ".txt");
        try (FileWriter fr = new FileWriter(testFile)) {
            String header = """
                    id,type,name,description,status,info
                    1,TASK,Первая задача,Описание первой задачи,NEW,
                    4,SUBTASK,Подзадача один,Первая подзадача первого эпика,NEW,3
                    3,EPIC,Первый эпик,Описание первого эпика,NEW,4
                    """;
            fr.write(header);
        }
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);
        assertEquals(3,
                manager.getTasks().size() + manager.getSubTasks().size() + manager.getEpics().size(),
                "Размер загруженных задач в менеджере не совпадает с ожидаемым");
    }

    @Test
    void checkSaveFile() throws IOException {
        File testFile = File.createTempFile("test", ".txt");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);
        manager.createTask(new Task("Первая задача", "Описание первой задачи"));
        manager.createTask(new Task("Первая задача", "Описание первой задачи"));
        manager.createTask(new Task("Первая задача", "Описание первой задачи"));
        manager.createTask(new Task("Первая задача", "Описание первой задачи"));

        assertEquals("""
                        id,type,name,description,status,info
                        1,TASK,Первая задача,Описание первой задачи,NEW,
                        2,TASK,Первая задача,Описание первой задачи,NEW,
                        3,TASK,Первая задача,Описание первой задачи,NEW,
                        4,TASK,Первая задача,Описание первой задачи,NEW,
                        """,
                Files.readString(testFile.toPath()),
                "Сохраненный менеджером файл не соответствует ожидаемому");
    }
}