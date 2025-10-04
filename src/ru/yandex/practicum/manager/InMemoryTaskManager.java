package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        tasks.values().forEach(task -> {
            history.remove(task.getId());
            prioritizedTasks.remove(task);
        });
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.values().forEach(task -> history.remove(task.getId()));
        epics.clear();
        subTasks.values().forEach(subTask -> {
            history.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        });
        subTasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        subTasks.values().forEach(subTask -> {
            history.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        });
        subTasks.clear();
        epics.values().forEach( epic -> {
            epic.clearSubTasks();
            updateEpicStatus(epic.getId());
            updateEpicTime(epic.getId());
        });
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
        if (!haveTimeSlot(task)) {
            System.out.println("Время новой задачи пересекается с другими задачами. Создание прервано");
            return 0;
        }
        int taskId = taskCounter;
        task.setId(taskId);
        tasks.put(taskId, task);
        if (task.getStartTime() != null) prioritizedTasks.add(task);
        increaseTaskCounter();
        return taskId;
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
        int subTaskId = taskCounter;
        int epicId = subTask.getEpicId();
        if (subTaskId == epicId) {
            System.out.println("Идентификатор подзадачи равен указанному идентификатору связанного эпика. " +
                    "Создание прервано");
            return 0;
        }
        if (!isExistingTask("Epic", epicId)) {
            System.out.println("Указанного в подзадаче эпика с идентификатором " + epicId + " не существует. " +
                    "Создание прервано");
            return 0;
        }
        if (!haveTimeSlot(subTask)) {
            System.out.println("Время новой подзадачи пересекается с другими задачами. Создание прервано");
            return 0;
        }
        subTask.setId(subTaskId);
        subTasks.put(subTaskId, subTask);
        epics.get(epicId).addSubTask(subTaskId);
        updateEpicStatus(epicId);
        if (subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
            updateEpicTime(epicId);
        }
        increaseTaskCounter();
        return subTaskId;
    }

    @Override
    public void updateTask(Task task) {
        int taskId = task.getId();
        if (!isExistingTask("Task", taskId)) {
            System.out.println("задачи с идентификатором " + taskId + " нет в списке задач. " +
                    "Обновление прервано");
            return;
        }
        if (!haveTimeSlot(task)) {
            System.out.println("Обновленное время задачи пересекается с другими задачами. Обновление прервано");
            return;
        }
        tasks.put(taskId, task);
        prioritizedTasks.remove(task);
        if (task.getStartTime() != null) prioritizedTasks.add(task);
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
            System.out.println("Подзадачи с идентификатором " + subTaskId + " нет в списке подзадач. " +
                    "Обновление прервано");
            return;
        }
        if (!haveTimeSlot(subTask)) {
            System.out.println("Обновленное время подзадачи пересекается с другими задачами. Обновление прервано");
            return;
        }
        int epicId = subTask.getEpicId();
        subTasks.put(subTaskId, subTask);
        updateEpicStatus(epicId);
        prioritizedTasks.remove(subTask);
        if (subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
            updateEpicTime(epicId);
        }
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
        epics.get(epicId).getSubtasksIds().forEach(subTask -> {
            subTasks.remove(subTask);
            history.remove(subTask);
        });
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
    public List<SubTask> getEpicSubTasks(int id) {
        Epic epic = getEpicById(id);
        if (epic != null) {
            return epic.getSubtasksIds()
                    .stream()
                    .map(subTasks::get)
                    .toList();
        }
        System.out.println("Эпика с идентификатором " + id + " не существует");
        return null;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
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
        } else if (epic.getSubtasksIds().isEmpty() || epic.getSubtasksIds().stream()
                .allMatch(subTaskId -> subTasks.get(subTaskId)
                .getStatus().equals(TaskStatus.NEW))) {
            epic.setStatus(TaskStatus.NEW);
        } else if (epic.getSubtasksIds().stream().allMatch(subTaskId -> subTasks.get(subTaskId)
                .getStatus().equals(TaskStatus.DONE))) {
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

        List<Optional<SubTask>> datedSubTasks = epic.getSubtasksIds()
                .stream()
                .map(subTasks::get)
                .filter(subTask -> subTask.getStartTime() != null)
                .peek(subTask -> epic.setDuration(epic.getDuration().plus(subTask.getDuration())))
                .collect(Collectors.teeing(
                        Collectors.minBy(Comparator.comparing(Task::getStartTime)),
                        Collectors.maxBy(Comparator.comparing(Task::getStartTime)),
                        List::of
                ));
        datedSubTasks.getFirst().ifPresentOrElse(subTask -> epic.setStartTime(subTask.getStartTime()),
                () -> epic.setStartTime(null));
        datedSubTasks.getLast().ifPresentOrElse(subTask -> epic.setEndTime(subTask.getEndTime()),
                () -> epic.setEndTime(null));

    }

    private boolean isOverlapped(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();

        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return (start1.isEqual(start2) || start1.isBefore(start2)) && end1.isAfter(start2)
                || (start2.isEqual(start1) || start2.isBefore(start1)) && end2.isAfter(start1);
    }

    private boolean haveTimeSlot(Task task) {
        return getPrioritizedTasks()
                .stream()
                .filter(pTask -> !pTask.equals(task))
                .noneMatch(pTask -> isOverlapped(pTask, task));
    }
}
