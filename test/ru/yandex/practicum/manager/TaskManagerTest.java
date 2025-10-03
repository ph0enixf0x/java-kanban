package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

abstract class TaskManagerTest<T extends TaskManager>
{
    protected T manager;
    protected Task expectedTask;
    protected Epic expectedEpic;
    protected SubTask expectedSubTask;


    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        System.out.println("--- Выпоняется тест: " + testInfo.getDisplayName());
        expectedTask = new Task("Первая задача", "Описание первой задачи",
                LocalDateTime.now(), Duration.ofMinutes(60));
        expectedEpic = new Epic("Первый эпик", "Описание первого эпика");
        expectedSubTask = new SubTask("Подзадача один",
                "Первая подзадача первого эпика",
                LocalDateTime.now().plusDays(1), Duration.ofMinutes(60), 1);
    }

    @Test
    void createTaskAndGetTaskById() {
        int taskId = manager.createTask(expectedTask);
        Task resultTask = manager.getTaskById(taskId);
        assertNotNull(resultTask, "Задача не найдена");
        assertEquals(expectedTask, resultTask,
                "Возвращенная задача несоответствует ожидаемой");
    }

    @Test
    void createEpicAndGetEpicById() {
        int epicId = manager.createEpic(expectedEpic);
        Epic resultEpic = manager.getEpicById(epicId);
        assertNotNull(resultEpic, "Эпик не найден");
        assertEquals(expectedEpic, resultEpic,
                "Возвращенный эпик несоответствует ожидаемому");
    }

    @Test
    void createSubtaskAndGetSubtaskById() {
        manager.createEpic(expectedEpic);
        int subTaskId = manager.createSubTask(expectedSubTask);
        SubTask resultSubtask = manager.getSubTaskById(subTaskId);
        assertNotNull(resultSubtask, "Подзадача не найдена");
        assertEquals(expectedSubTask, resultSubtask,
                "Возвращенная подзадача несоответствует ожидаемой");
    }

    @Test
    void checkAddingSubtaskAsSelfEpicRestriction() {
        int subTaskId = manager.createSubTask(expectedSubTask);
        assertEquals(0, subTaskId,
                "Должно быть нельзя сделать подзадачу своим же эпиком");
    }

    @Test
    void checkAddedTaskFields() {
        Task task = manager.getTaskById(manager.createTask(expectedTask));
        assertEquals(1, task.getId(), "Неожиданный идентификатор задачи");
        assertEquals("Первая задача", task.getName(), "Неожиданное имя задачи");
        assertEquals("Описание первой задачи", task.getDescription(), "Неожиданное описание задачи");
    }

    @Test
    void getAllTasks() {
        manager.createEpic(expectedEpic);
        manager.createTask(expectedTask);
        manager.createSubTask(expectedSubTask);

        assertEquals(1, manager.getTasks().size(),
                "Не правильное количество сохраненных в менеджере задач");
        assertEquals(1, manager.getEpics().size(),
                "Не правильное количество сохраненных в менеджере эпиков");
        assertEquals(1, manager.getSubTasks().size(),
                "Не правильное количество сохраненных в менеджере подзадач");
    }

    @Test
    void deleteDifferentTasks() {
        int epicId = manager.createEpic(expectedEpic);
        int taskId = manager.createTask(expectedTask);
        int subtaskId = manager.createSubTask(expectedSubTask);
        manager.deleteSubTaskById(subtaskId);
        manager.deleteEpicById(epicId);
        manager.deleteTaskById(taskId);

        assertNull(manager.getTaskById(taskId), "Задача должна быть удалена");
        assertNull(manager.getEpicById(epicId), "Эпик должен быть удален");
        assertNull(manager.getSubTaskById(subtaskId), "Подзадача должна быть удалена");
    }

    @Test
    void deleteAllDifferentTasks() {
        manager.createEpic(expectedEpic);
        manager.createTask(expectedTask);
        manager.createSubTask(expectedSubTask);

        manager.deleteTasks();
        manager.deleteSubTasks();
        manager.deleteEpics();

        assertEquals(0, manager.getTasks().size(), "Все задачи должны быть удалены");
        assertEquals(0, manager.getEpics().size(), "Все эпики должны быть удалены");
        assertEquals(0, manager.getSubTasks().size(), "Все подзадачи должны быть удалены");
    }

    @Test
    void deleteSubtaskAfterDeletingAllEpics() {
        manager.createEpic(expectedEpic);
        manager.createSubTask(expectedSubTask);

        manager.deleteEpics();
        assertEquals(0, manager.getEpics().size(), "Все эпики должны быть удалены");
        assertEquals(0, manager.getSubTasks().size(), "Все подзадачи должны быть удалены");
    }

    @Test
    void deleteSubtasksOnEpicDelete() {
        int epicId = manager.createEpic(expectedEpic);
        manager.createSubTask(expectedSubTask);
        manager.deleteEpicById(epicId);
        assertEquals(0, manager.getSubTasks().size(),
                "Подзадачи удаленного эпика должны были быть удалены");
    }

    @Test
    void updateDifferentTasks() {

        int epicId = manager.createEpic(expectedEpic);
        int taskId = manager.createTask(expectedTask);
        int subTaskId = manager.createSubTask(expectedSubTask);
        Epic epic = manager.getEpicById(epicId);
        Task task = manager.getTaskById(taskId);
        SubTask subTask = manager.getSubTaskById(subTaskId);

        epic.setName("Другое название эпика");
        task.setName("Странное название задачи");
        subTask.setDescription("Другое описание подзадачи");

        manager.updateEpic(epic);
        manager.updateTask(task);
        manager.updateSubTask(subTask);

        assertEquals("Другое название эпика", manager.getEpicById(epicId).getName(),
                "Название эпика не изменилось");
        assertEquals("Странное название задачи", manager.getTaskById(taskId).getName(),
                "Название эпика не изменилось");
        assertEquals("Другое описание подзадачи", manager.getSubTaskById(subTaskId).getDescription(),
                "Описание подзадачи не изменилось");
    }

    @Test
    void checkEpicStatusUpdate() {
        int epicId = manager.createEpic(expectedEpic);
        int subTaskId = manager.createSubTask(expectedSubTask);

        SubTask subTask = manager.getSubTaskById(subTaskId);
        subTask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(subTask);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epicId).getStatus(),
                "Статус эпика должен был измениться на IN_PROGRESS");

        subTask.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subTask);

        assertEquals(TaskStatus.DONE, manager.getEpicById(epicId).getStatus(),
                "Статус эпика должен был измениться на DONE");

        manager.createSubTask(new SubTask("Другая подзадача",
                "Другое описание",
                LocalDateTime.now().plusDays(2), Duration.ofMinutes(60), 1));

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epicId).getStatus(),
                "Статус эпика должен был измениться на IN_PROGRESS");

        manager.deleteSubTaskById(subTaskId);

        assertEquals(TaskStatus.NEW, manager.getEpicById(epicId).getStatus(),
                "Статус эпика должен был измениться на NEW");
    }

    @Test
    void getEpicSubTasks() {
        int epicId = manager.createEpic(expectedEpic);
        manager.createSubTask(expectedSubTask);

        assertEquals(1, manager.getEpicSubTasks(epicId).size(),
                "Неверное количество подзадач у эпика");
    }
}
