package ru.yandex.practicum.manager;

import org.testng.internal.collections.Pair;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int taskCounter;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final HashMap<Integer, Epic> epics;
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private final HistoryManager history;

    public InMemoryTaskManager() {
        this.taskCounter = 1;
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        history = new Managers().getDefaultHistory();
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст!");
            return;
        }
        for (Task task : tasks.values()) {
            history.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        if (epics.isEmpty()) {
            System.out.println("Список эпиков пуст!");
            return;
        }
        for (Task task : epics.values()) {
            history.remove(task.getId());
        }
        epics.clear();
        if (subTasks.isEmpty()) {
            System.out.println("При удалении эпиков обнаружили, что подзадач у них нет");
            return;
        }
        for (Task task : subTasks.values()) {
            history.remove(task.getId());
        }
        subTasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        if (subTasks.isEmpty()) {
            System.out.println("Список подзадач пуст!");
            return;
        }
        for (Task task : subTasks.values()) {
            history.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            int epicId = epic.getId();
            updateEpicStatus(epicId);
            updateEpicTime(epicId);
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        if (isExistingTask("Task", taskId)) {
            Task task = tasks.get(taskId);
            history.add(task);
            return task;
        }
        System.out.println("Задачи с идентификатором " + taskId + " не существует");
        return null;
    }

    @Override
    public Epic getEpicById(int epicId) {
        if (isExistingTask("Epic", epicId)) {
            Epic epic = epics.get(epicId);
            history.add(epic);
            return epic;
        }
        System.out.println("Эпика с идентификатором " + epicId + " не существует");
        return null;
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        if (isExistingTask("SubTask", subTaskId)) {
            SubTask subTask = subTasks.get(subTaskId);
            history.add(subTask);
            return subTask;
        }
        System.out.println("Подзадачи с идентификатором " + subTaskId + " не существует");
        return null;
    }

    @Override
    public int createTask(Task task) {
        task.setId(taskCounter);
        tasks.put(taskCounter, task);
        if (checkOverlaps(task)) {
            System.out.println("Задача пересекается с другой задачей");
            return 0;
        }
        prioritizeTask(task);
        increaseTaskCounter();
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(taskCounter);
        epics.put(taskCounter, epic);
        increaseTaskCounter();
        return epic.getId();
    }

    @Override
    public int createSubTask(SubTask subTask) {
        subTask.setId(taskCounter);
        int epicId = subTask.getEpicId();
        if (epicId == subTask.getId()) {
            System.out.println("Нельзя сделать подзадачу своим собственным эпиком!");
            return 0;
        }
        if (!isExistingTask("Epic", epicId)) {
            System.out.println("Эпика с идентификатором " + epicId + " указанным в подзадаче не существует");
            return 0;
        }
        if (checkOverlaps(subTask)) {
            System.out.println("Задача пересекается с другой задачей");
            return 0;
        }
        subTasks.put(taskCounter, subTask);
        epics.get(epicId).addSubTask(taskCounter);
        updateEpicStatus(epicId);
        updateEpicTime(epicId);
        prioritizeTask(subTask);
        increaseTaskCounter();
        return subTask.getId();
    }

    @Override
    public void updateTask(Task task) {
        int taskId = task.getId();
        if (!isExistingTask("Task", taskId)) {
            System.out.println("Задачи с идентификатором " + taskId + " не существует");
            return;
        }
        if (checkOverlaps(task)) {
            System.out.println("Задача пересекается с другой задачей");
        }
        prioritizedTasks.remove(task);
        prioritizeTask(task);
        tasks.put(taskId, task);
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicId = epic.getId();
        if (!isExistingTask("Epic", epicId)) {
            System.out.println("Эпика с идентификатором " + epicId + " не существует");
            return;
        }
        Epic updatedEpic = epics.get(epicId);
        updatedEpic.setName(epic.getName());
        updatedEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int subTaskId = subTask.getId();
        if (!isExistingTask("SubTask", subTaskId)) {
            System.out.println("Подзадачи с идентификатором " + subTaskId + " не существует");
            return;
        }
        if (checkOverlaps(subTask)) {
            System.out.println("Задача пересекается с другой задачей");
        }
        prioritizedTasks.remove(subTask);
        prioritizeTask(subTask);
        subTasks.put(subTaskId, subTask);
        int epicId = subTask.getEpicId();
        updateEpicStatus(epicId);
        updateEpicTime(epicId);
    }

    @Override
    public void deleteTaskById(int taskId) {
        if (!tasks.containsKey(taskId)) {
            System.out.println("Задачи с идентификатором " + taskId + " не существует");
            return;
        }
        prioritizedTasks.remove(tasks.get(taskId));
        tasks.remove(taskId);
        history.remove(taskId);

    }

    @Override
    public void deleteEpicById(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпика с идентификатором " + epicId + " не существует");
            return;
        }
        for (int subtaskId : epics.get(epicId).getSubtasksIds()) {
            subTasks.remove(subtaskId);
            history.remove(subtaskId);
        }
        epics.remove(epicId);
        history.remove(epicId);
    }

    @Override
    public void deleteSubTaskById(int subTaskId) {
        if (!subTasks.containsKey(subTaskId)) {
            System.out.println("Подзадачи с идентификатором " + subTaskId + " не существует.");
            return;
        }
        int epicId = subTasks.get(subTaskId).getEpicId();
        epics.get(epicId).removeSubTask(subTaskId);
        updateEpicStatus(epicId);
        prioritizedTasks.remove(subTasks.get(subTaskId));
        subTasks.remove(subTaskId);
        history.remove(subTaskId);
        updateEpicTime(epicId);
    }

    @Override
    public ArrayList<SubTask> getEpicSubTasks(int id) {
        Epic epic = getEpicById(id);
        ArrayList<SubTask> result = new ArrayList<>();
        if (epic != null) {
            ArrayList<Integer> subtaskIds = epic.getSubtasksIds();
            for (int subtaskId : subtaskIds) {
                result.add(subTasks.get(subtaskId));
            }
            return result;
        }
        System.out.println("Эпика с идентификатором " + id + " не существует");
        return null;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void increaseTaskCounter() {
        taskCounter += 1;
    }

    private boolean isExistingTask(String type, int id) {
        return switch (type) {
            case "Task" -> tasks.containsKey(id);
            case "Epic" -> epics.containsKey(id);
            case "SubTask" -> subTasks.containsKey(id);
            default -> {
                System.out.println("Неизвестный тип задачи: " + type);
                yield false;
            }
        };
    }

    private void updateEpicStatus(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            System.out.println("Эпика с идентификатором " + id + " не существует");
            return;
        }
        ArrayList<Integer> subTaskIds = epic.getSubtasksIds();
        if (subTaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean haveDoneTasks = false;
        boolean haveInProgressTasks = false;
        boolean haveNewTasks = false;
        for (int subTaskId : subTaskIds) {
            TaskStatus taskStatus = subTasks.get(subTaskId).getStatus();
            if (taskStatus.equals(TaskStatus.DONE)) {
                haveDoneTasks = true;
            } else if (taskStatus.equals(TaskStatus.IN_PROGRESS)) {
                haveInProgressTasks = true;
            } else if (taskStatus.equals(TaskStatus.NEW)) {
                haveNewTasks = true;
            }
        }
        if (!haveDoneTasks && !haveInProgressTasks) {
            epic.setStatus(TaskStatus.NEW);
        } else if (haveDoneTasks && !haveInProgressTasks && !haveNewTasks) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void updateEpicTime(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            System.out.println("Эпика с идентификатором " + id + " не существует");
            return;
        }
        epic.setDuration(Duration.ZERO);
        Pair<Optional<SubTask>, Optional<SubTask>> datedSubTasks = epic.getSubtasksIds()
                .stream()
                .map(subTasks::get)
                .filter(subTask -> subTask.getStartTime() != null)
                .peek(subTask -> epic.setDuration(epic.getDuration().plus(subTask.getDuration())))
                .collect(Collectors.teeing(
                        Collectors.minBy(Comparator.comparing(Task::getStartTime)),
                        Collectors.maxBy(Comparator.comparing(Task::getStartTime)),
                        Pair::new
                ));
        datedSubTasks.first().ifPresentOrElse(subTask -> epic.setStartTime(subTask.getStartTime()),
                () -> epic.setStartTime(null));
        datedSubTasks.second().ifPresentOrElse(subTask -> epic.setEndTime(subTask.getEndTime()),
                () -> epic.setEndTime(null));

    }

    private void prioritizeTask(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        prioritizedTasks.add(task);
    }

    private boolean isOverlapped(Task task1, Task task2) {
        return task1.getEndTime().isAfter(task2.getStartTime());
    }

    private boolean checkOverlaps(Task task) {
        return getPrioritizedTasks()
                .stream()
                .anyMatch(pTask -> isOverlapped(task, pTask));
    }
}
