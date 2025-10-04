package ru.yandex.practicum.manager;

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

    void beforeEach(TestInfo testInfo) {
        System.out.println("--- Выполняется тест: " + testInfo.getDisplayName());
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

        assertEquals(TaskStatus.NEW, manager.getEpicById(epicId).getStatus(),
                "Статус эпика должен быть NEW");

        SubTask subTask = manager.getSubTaskById(subTaskId);
        subTask.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subTask);

        assertEquals(TaskStatus.DONE, manager.getEpicById(epicId).getStatus(),
                "Статус эпика должен был измениться на DONE");

        int subTaskId2 = manager.createSubTask(new SubTask("Вторая подзадача", "Новая тестовая подзадача",
                LocalDateTime.now().plusDays(2), Duration.ofMinutes(60), epicId));

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epicId).getStatus(),
                "Статус эпика должен был измениться на IN_PROGRESS");

        subTask.setStatus(TaskStatus.IN_PROGRESS);
        SubTask subTask2 = manager.getSubTaskById(subTaskId2);
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(subTask);
        manager.updateSubTask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epicId).getStatus(),
                "Статус эпика должен был остаться IN_PROGRESS");
    }

    @Test
    void checkEpicSubTasks() {
        int epicId = manager.createEpic(expectedEpic);
        manager.createSubTask(expectedSubTask);

        assertEquals(1, manager.getEpicSubTasks(epicId).size(),
                "Неверное количество подзадач у эпика");
    }

    @Test
    void checkOverlappedTasks() {
        int taskId = manager.createTask(expectedTask);
        manager.createTask(new Task("Вторая задача", "Задача с пересекающейся датой",
                LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(60)));
        assertEquals(1, manager.getTasks().size(),
                "Не корректное количество задач в менеджере после добавления пересекающейся задачи");

        Task task = manager.getTaskById(taskId);
        LocalDateTime updatedTime = task.getStartTime().plusMinutes(10);
        task.setStartTime(updatedTime);
        manager.updateTask(task);
        assertEquals(updatedTime, manager.getTaskById(taskId).getStartTime(),
                "Не корректное время у обновленной задачи");
    }

    @Test
    void checkOverlappedSubTasks() {
        int epicId = manager.createEpic(expectedEpic);
        int subTaskId = manager.createSubTask(expectedSubTask);
        manager.createSubTask(new SubTask("Вторая подзадача", "Подзадача с пересекающимся временем",
                LocalDateTime.now().plusDays(1).plusMinutes(59), Duration.ofMinutes(60), epicId));
        assertEquals(1, manager.getSubTasks().size(),
                "Не корректное количество подзадач в менеджере после добавления пересекающейся подзадачи");

        SubTask subTask = manager.getSubTaskById(subTaskId);
        LocalDateTime updatedTime = subTask.getStartTime().plusMinutes(10);
        subTask.setStartTime(updatedTime);
        manager.updateSubTask(subTask);
        assertEquals(updatedTime, manager.getSubTaskById(subTaskId).getStartTime(),
                "Не корректное время у обновленной подзадачи");
    }
}
