package ru.yandex.practicum.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
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
    void checkLoadTaskFromFile() throws IOException {
        File testFile = File.createTempFile("test", ".txt");
        try (FileWriter fr = new FileWriter(testFile)) {
            String header = """
id,type,name,status,description,epic
1,TASK,Первая задача,DONE,Описание первой задачи,
                    """;
            fr.write(header);
        }
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);
        assertTrue(manager.tasks.containsKey(1),
                "Задача из файла сохранения не загрузилась");
        Task task = manager.getTaskById(1);
        assertEquals("Первая задача", task.getName(),
                "Название загруженной задачи не соответствует ожидаемому");
        assertEquals("Описание первой задачи", task.getDescription(),
                "Описание загруженной задачи не соответствует ожидаемому");
        assertEquals(TaskStatus.DONE, task.getStatus(),
                "Статус загруженной задачи не соответствует ожидаемому");
    }

    @Test
    void checkLoadEpicFromFile() throws IOException {
        File testFile = File.createTempFile("test", ".txt");
        try (FileWriter fr = new FileWriter(testFile)) {
            String header = """
id,type,name,status,description,epic
3,EPIC,Первый эпик,IN_PROGRESS,Описание первого эпика,
5,SUBTASK,Подзадача два,IN_PROGRESS,Вторая подзадача первого эпика,3
6,SUBTASK,Подзадача три,NEW,Третья подзадача первого эпика,3
                    """;
            fr.write(header);
        }
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);
        assertTrue(manager.epics.containsKey(3),
                "Эпик из файла сохранения не загрузился");
        Epic epic = manager.getEpicById(3);
        assertEquals("Первый эпик", epic.getName(),
                "Название загруженного эпика не соответствует ожидаемому");
        assertEquals("Описание первого эпика", epic.getDescription(),
                "Описание загруженного эпика не соответствует ожидаемому");
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Статус загруженного эпика не соответствует ожидаемому");
        assertEquals(List.of(5,6), epic.getSubtasksIds(),
                "Подзадачи загруженного эпика не соответствуют ожидаемым");
    }

    @Test
    void checkLoadSubTaskFromFile() throws IOException {
        File testFile = File.createTempFile("test", ".txt");
        try (FileWriter fr = new FileWriter(testFile)) {
            String header = """
id,type,name,status,description,epic
3,EPIC,Первый эпик,NEW,Описание первого эпика,
6,SUBTASK,Подзадача три,NEW,Третья подзадача первого эпика,3
                    """;
            fr.write(header);
        }
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);
        assertTrue(manager.subTasks.containsKey(6),
                "Подзадача из файла сохранения не загрузилась");
        SubTask subTask = manager.getSubTaskById(6);
        assertEquals("Подзадача три", subTask.getName(),
                "Название загруженной подзадачи не соответствует ожидаемому");
        assertEquals("Третья подзадача первого эпика", subTask.getDescription(),
                "Описание загруженной подзадачи не соответствует ожидаемому");
        assertEquals(TaskStatus.NEW, subTask.getStatus(),
                "Статус загруженной подзадачи не соответствует ожидаемому");
        assertEquals(3, subTask.getEpicId(),
                "Номер эпика в загруженной подзадаче не соответствует ожидаемому");
    }

    @Test
    void checkLoadTaskCounterFromFile() throws IOException {
        File testFile = File.createTempFile("test", ".txt");
        try (FileWriter fr = new FileWriter(testFile)) {
            String header = """
id,type,name,status,description,epic
2,EPIC,Первый эпик,NEW,Описание первого эпика,
12,SUBTASK,Подзадача три,NEW,Третья подзадача первого эпика,2
                    """;
            fr.write(header);
        }
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);
        assertEquals(13, manager.taskCounter,
                "Номер следующей задачи не соответствует ожидаемому");
    }

    @Test
    void checkSaveFile() throws IOException {
        File testFile = File.createTempFile("test", ".txt");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);
        manager.createTask(new Task("Первая задача", "Описание первой задачи"));
        manager.createTask(new Task("Вторая задача", "Описание второй задачи"));
        manager.createTask(new Task("Третья задача", "Описание третьей задачи"));
        int epicId1 = manager.createEpic(new Epic("Первый эпик", "Описание первого эпика"));
        manager.createSubTask(new SubTask("Первая подзадача", "Подзадача первого эпика", epicId1));
        manager.createSubTask(new SubTask("Вторая подзадача", "Подзадача первого эпика", epicId1));
        int epicId2 = manager.createEpic(new Epic("Второй эпик", "Описание второго эпика"));
        manager.createSubTask(new SubTask("Первая подзадача", "Подзадача второго эпика", epicId2));

        assertEquals("""
                        id,type,name,status,description,epic
                        1,TASK,Первая задача,NEW,Описание первой задачи,
                        2,TASK,Вторая задача,NEW,Описание второй задачи,
                        3,TASK,Третья задача,NEW,Описание третьей задачи,
                        4,EPIC,Первый эпик,NEW,Описание первого эпика,
                        7,EPIC,Второй эпик,NEW,Описание второго эпика,
                        5,SUBTASK,Первая подзадача,NEW,Подзадача первого эпика,4
                        6,SUBTASK,Вторая подзадача,NEW,Подзадача первого эпика,4
                        8,SUBTASK,Первая подзадача,NEW,Подзадача второго эпика,7
                        """,
                Files.readString(testFile.toPath()),
                "Сохраненный менеджером файл не соответствует ожидаемому");
    }

    @Test
    void checkRestoreFromSaveFile() throws IOException {
        File testFile = File.createTempFile("test", ".txt");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);
        int taskId = manager.createTask(new Task("Первая задача", "Описание первой задачи"));
        int epicId = manager.createEpic(new Epic("Первый эпик", "Описание первого эпика"));
        int subTaskId = manager.createSubTask(new SubTask("Первая подзадача", "Подзадача первого эпика", epicId));
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(manager.getSaveFile());
        assertEquals("Первая задача", manager2.getTaskById(taskId).getName(),
                "Название полученное задачи не соответствует ожидаемому");
        assertEquals("Описание первого эпика", manager2.getEpicById(epicId).getDescription(),
                "Описание полученного эпика не соответствует ожидаемому");
        assertEquals(epicId, manager2.getSubTaskById(subTaskId).getEpicId(),
                "Номер эпика полученной подзадачи не соответствует ожидаемому");
        assertEquals(4, manager2.taskCounter,
                "Номер следующей задачи в загруженном менеджере не соответствует ожидаемому");
    }
}