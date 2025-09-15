package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.*;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = new FileBackedTaskManager();
    }

    @Test
    void convertTaskToString() {
        assertEquals("0,TASK,Первая задача,Описание первой задачи,NEW,",
                manager.toString(new Task("Первая задача",
                        "Описание первой задачи")),
                "Полученная строка задачи не соответствует ожидаемому результату");
    }

    @Test
    void convertSubTaskToString() {
        assertEquals("0,SUBTASK,Подзадача один,Первая подзадача первого эпика,NEW,3",
                manager.toString(new SubTask("Подзадача один",
                        "Первая подзадача первого эпика",
                        3)),
                "Полученная строка подзадачи не соответствует ожидаемому результату");
    }

    @Test
    void convertEmptyEpicToString() {
        assertEquals("0,EPIC,Первый эпик,Описание первого эпика,NEW,",
                manager.toString(new Epic("Первый эпик", "Описание первого эпика")),
                "Полученная строка пустого эпика не соответствует ожидаемому результату");
    }

    @Test
    void convertFilledEpicToString() {
        Epic filledEpic = new Epic("Первый эпик", "Описание первого эпика");
        filledEpic.addSubTask(3);
        filledEpic.addSubTask(5);
        assertEquals("0,EPIC,Первый эпик,Описание первого эпика,NEW,3,5",
                manager.toString(filledEpic),
                "Полученная строка эпика с подзадачами не соответствует ожидаемому результату");
    }

    @Test
    void checkConvertedTaskId() {
        assertEquals(4,
                manager.fromString("4,TASK,Первая задача,Описание первой задачи,NEW,").getId(),
                "Идентификатор сгенерированной задачи не соответствует ожидаемому");
    }

    @Test
    void checkConvertedTaskType() {
        assertEquals(TaskType.TASK,
                manager.fromString("4,TASK,Первая задача,Описание первой задачи,NEW,").getType(),
                "Тип сгенерированной задачи не соответствует ожидаемому");
    }

    @Test
    void checkConvertedTaskName() {
        assertEquals("Первая задача",
                manager.fromString("4,TASK,Первая задача,Описание первой задачи,NEW,").getName(),
                "Название сгенерированной задачи не соответствует ожидаемому");
    }

    @Test
    void checkConvertedTaskDescription() {
        assertEquals("Описание первой задачи",
                manager.fromString("4,TASK,Первая задача,Описание первой задачи,NEW,").getDescription(),
                "Описание сгенерированной задачи не соответствует ожидаемому");
    }
    @Test
    void checkConvertedTaskStatus() {
        assertEquals(TaskStatus.NEW,
                manager.fromString("4,TASK,Первая задача,Описание первой задачи,NEW,").getStatus(),
                "Статус сгенерированной задачи не соответствует ожидаемому");
    }

    @Test
    void checkConvertedSubTaskEpicId() {
        SubTask subTask = (SubTask) manager.fromString("4,SUBTASK,Подзадача один,Первая подзадача первого эпика,NEW,12");
        assertEquals(12,
                subTask.getEpicId(),
                "Идентификатор эпика сгенерированной подзадачи не соответствует ожидаемому");
    }

    @Test
    void checkConvertedEpicSubtaskIds() {
        Epic epic = (Epic) manager.fromString("3,EPIC,Первый эпик,Описание первого эпика,NEW,4,5,6");
        assertEquals("[4, 5, 6]",
                epic.getSubtasksIds().toString(),
                "Список идентификаторов подзадач сгенерированного эпика не соответствует ожидаемому");
    }
}