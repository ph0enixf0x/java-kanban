package ru.yandex.practicum.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void checkIdenticalSubTasks() {
        SubTask subTask1 = new SubTask("Подзадача один", "Первая подзадача первого эпика", 1);
        SubTask subTask2 = new SubTask("Такая же подзадача", "У этой подзадачи такой же id", 1);
        assertEquals(subTask2, subTask1, "Подзадачи с одинаковым id должны быть идентичны");
    }

    @Test
    void getSubTaskAsString() {
        assertEquals("0,SUBTASK,Тестовая подзадача,NEW,Описание тестовой подзадачи,0",
                SubTask.toString(new SubTask("Тестовая подзадача", "Описание тестовой подзадачи", 0)),
                "Строка новой подзадачи не совпадает с ожидаемой");
    }

    @Test
    void getModifiedSubTaskAsString() {
        SubTask task = new SubTask("Тестовая подзадача[] 1", "Описание тестов$#$ой подзадачи", 0);
        task.setId(4);
        task.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals("4,SUBTASK,Тестовая подзадача[] 1,IN_PROGRESS,Описание тестов$#$ой подзадачи,0",
                SubTask.toString(task),
                "Строка измененной подзадачи не совпадает с ожидаемой");
    }

    @Test
    void getModifiedSubTaskEpicIdFromString() {
        assertEquals(21,
                SubTask.fromString("4,SUBTASK,Тестовая подзадача[] 1,IN_PROGRESS,Описание тестов$#$ой подзадачи,21")
                        .getEpicId(),
                "Идентификатор эпика модифицированной подзадачи не соответствует ожидаемому");
    }

}