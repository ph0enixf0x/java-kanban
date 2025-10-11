package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<SubTask> getSubTasks();

    List<Epic> getEpics();

    void deleteTasks();

    void deleteEpics();

    void deleteSubTasks();

    Task getTaskById(int taskId);

    Epic getEpicById(int epicId);

    SubTask getSubTaskById(int subTaskId);

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubTask(SubTask subTask);

    int updateTask(Task task);

    int updateEpic(Epic epic);

    int updateSubTask(SubTask subTask);

    int deleteTaskById(int taskId);

    int deleteEpicById(int epicId);

    int deleteSubTaskById(int subTaskId);

    List<SubTask> getEpicSubTasks(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
