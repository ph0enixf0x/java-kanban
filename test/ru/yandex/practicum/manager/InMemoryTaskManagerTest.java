package ru.yandex.practicum.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager manager;
    private static Task expectedTask1;
    private static Epic expectedEpic1;
    private static SubTask expectedSubTask1;

    @BeforeAll
    static void beforeAll() {
        expectedTask1 = new Task("Первая задача", "Описание первой задачи",
                LocalDateTime.now(), Duration.ofMinutes(60));
        expectedEpic1 = new Epic("Первый эпик", "Описание первого эпика");
        expectedSubTask1 = new SubTask("Подзадача один",
                "Первая подзадача первого эпика",
                LocalDateTime.now(), Duration.ofMinutes(60), 1);
    }

    @BeforeEach
    void beforeEach() {
        manager = new InMemoryTaskManager();
    }

    @AfterEach
    void afterEach() {
        expectedTask1 = new Task("Первая задача", "Описание первой задачи",
                LocalDateTime.now(), Duration.ofMinutes(60));
        expectedEpic1 = new Epic("Первый эпик", "Описание первого эпика");
        expectedSubTask1 = new SubTask("Подзадача один",
                "Первая подзадача первого эпика",
                LocalDateTime.now(), Duration.ofMinutes(60), 1);
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

    @Test
    void getAllTasks() {
        manager.createEpic(expectedEpic1);
        manager.createEpic(expectedEpic1);

        manager.createTask(expectedTask1);
        manager.createTask(expectedTask1);

        manager.createSubTask(expectedSubTask1);
        manager.createSubTask(expectedSubTask1);

        assertEquals(2, manager.getTasks().size(),
                "Не правильное количество сохраненных в менеджере задач");
        assertEquals(2, manager.getEpics().size(),
                "Не правильное количество сохраненных в менеджере эпиков");
        assertEquals(2, manager.getSubTasks().size(),
                "Не правильное количество сохраненных в менеджере подзадач");
    }

    @Test
    void deleteDifferentTasks() {
        int epicId = manager.createEpic(expectedEpic1);
        int taskId = manager.createTask(expectedTask1);
        int subtaskId = manager.createSubTask(expectedSubTask1);

        assertNotNull(manager.getTaskById(taskId), "Задача не была корректно создана");
        assertNotNull(manager.getEpicById(epicId), "Эпик не был корректно создан");
        assertNotNull(manager.getSubTaskById(subtaskId), "Подзадача не была корректно создана");

        manager.deleteSubTaskById(subtaskId);
        manager.deleteEpicById(epicId);
        manager.deleteTaskById(taskId);

        assertNull(manager.getTaskById(taskId), "Задача должна быть удалена");
        assertNull(manager.getEpicById(epicId), "Эпик должен быть удален");
        assertNull(manager.getSubTaskById(subtaskId), "Подзадача должна быть удалена");
    }

    @Test
    void deleteAllDifferentTasks() {
        manager.createEpic(expectedEpic1);
        manager.createEpic(expectedEpic1);
        manager.createEpic(expectedEpic1);

        manager.createTask(expectedTask1);
        manager.createTask(expectedTask1);

        manager.createSubTask(expectedSubTask1);
        manager.createSubTask(expectedSubTask1);
        manager.createSubTask(expectedSubTask1);
        manager.createSubTask(expectedSubTask1);

        manager.deleteTasks();
        manager.deleteSubTasks();
        manager.deleteEpics();

        assertEquals(0, manager.getTasks().size(), "Все задачи должны быть удалены");
        assertEquals(0, manager.getEpics().size(), "Все эпики должны быть удалены");
        assertEquals(0, manager.getSubTasks().size(), "Все подзадачи должны быть удалены");
    }

    @Test
    void deleteSubtaskAfterDeletingAllEpics() {
        manager.createEpic(expectedEpic1);
        manager.createSubTask(expectedSubTask1);
        manager.createSubTask(expectedSubTask1);
        manager.createSubTask(expectedSubTask1);

        manager.deleteEpics();
        assertEquals(0, manager.getEpics().size(), "Все эпики должны быть удалены");
        assertEquals(0, manager.getSubTasks().size(), "Все подзадачи должны быть удалены");
    }

    @Test
    void deleteSubtasksOnEpicDelete() {
        int epicId = manager.createEpic(expectedEpic1);
        manager.createSubTask(expectedSubTask1);
        manager.createSubTask(expectedSubTask1);
        manager.createSubTask(expectedSubTask1);

        assertEquals(3, manager.getSubTasks().size(), "Подзадачи не были корректно созданы");

        manager.deleteEpicById(epicId);

        assertEquals(0, manager.getSubTasks().size(),
                "Подзадачи удаленного эпика должны были быть удалены");
    }

    @Test
    void updateDifferentTasks() {

        int epicId = manager.createEpic(expectedEpic1);
        int taskId = manager.createTask(expectedTask1);
        int subTaskId = manager.createSubTask(expectedSubTask1);
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
        int epicId = manager.createEpic(expectedEpic1);
        int subTaskId = manager.createSubTask(expectedSubTask1);

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
                LocalDateTime.now(), Duration.ofMinutes(60), 1));

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epicId).getStatus(),
                "Статус эпика должен был измениться на IN_PROGRESS");

        manager.deleteSubTaskById(subTaskId);

        assertEquals(TaskStatus.NEW, manager.getEpicById(epicId).getStatus(),
                "Статус эпика должен был измениться на NEW");
    }

    @Test
    void getEpicSubTasks() {
        int epicId = manager.createEpic(expectedEpic1);
        manager.createSubTask(expectedSubTask1);
        manager.createSubTask(expectedSubTask1);
        manager.createSubTask(expectedSubTask1);

        assertEquals(3, manager.getEpicSubTasks(epicId).size(),
                "Неверное количество подзадач у эпика");
    }

    @Test
    void getHistoryManagerIntegration() {
        int epicId = manager.createEpic(expectedEpic1);
        int taskId = manager.createTask(expectedTask1);
        int subTaskId = manager.createSubTask(expectedSubTask1);

        manager.getEpicById(epicId);
        manager.getTaskById(taskId);
        manager.getSubTaskById(subTaskId);

        assertEquals(3, manager.getHistory().size(),
                "Размер полученной истории не соответствует ожидаемому");
    }
}