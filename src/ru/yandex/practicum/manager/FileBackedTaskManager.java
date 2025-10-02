package ru.yandex.practicum.manager;

import ru.yandex.practicum.exception.ManagerLoadException;
import ru.yandex.practicum.exception.ManagerSaveException;
import ru.yandex.practicum.tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File saveFile;
    private static final String SAVE_FILE_HEADER = "id,type,name,status,description,start,duration,epic\n";

    public FileBackedTaskManager(File saveFile) {
        super();
        this.saveFile = saveFile;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public int createSubTask(SubTask subTask) {
        int id = super.createSubTask(subTask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteSubTaskById(int subTaskId) {
        super.deleteSubTaskById(subTaskId);
        save();
    }

    public File getSaveFile() {
        return saveFile;
    }

    private void save() {
        try (FileWriter file = new FileWriter(saveFile.toString())) {
            StringBuilder saveString = new StringBuilder(SAVE_FILE_HEADER);
            for (ArrayList<? extends Task> taskList : List.of(getTasks(), getEpics(), getSubTasks())) {
                for (Task task : taskList) {
                    saveString.append(toString(task)).append("\n");
                }
            }
            file.write(saveString.toString());
        } catch (Exception ex) {
            throw new ManagerSaveException("Возникла ошибка при работе сохранении состояния в файл", ex);
        }
    }

    public String toString(Task task) {
        StringBuilder taskString = new StringBuilder();
        taskString.append(task.getId()).append(",");
        taskString.append(task.getType()).append(",");
        taskString.append(task.getName()).append(",");
        taskString.append(task.getStatus()).append(",");
        taskString.append(task.getDescription()).append(",");
        if (task.getStartTime() != null) {
            taskString.append(task.getStartTime().truncatedTo(ChronoUnit.SECONDS)).append(",");
            taskString.append(task.getDuration().toMinutes()).append(",");
        } else {
            taskString.append(",");
            taskString.append(",");
        }
        if (task.getType().equals(TaskType.SUBTASK)) {
            taskString.append(((SubTask) task).getEpicId());
        }
        return taskString.toString();
    }

    private Task fromString(String value) {
        String[] splitValue = value.split(",");
        switch (TaskType.valueOf(splitValue[1])) {
            case TASK:
                Task generatedTask = new Task(splitValue[2], splitValue[4],
                        LocalDateTime.parse(splitValue[5]), Duration.ofMinutes(Integer.parseInt(splitValue[6])));
                generatedTask.setId(Integer.parseInt(splitValue[0]));
                generatedTask.setStatus(TaskStatus.valueOf(splitValue[3]));
                return generatedTask;
            case SUBTASK:
                SubTask generatedSubTask = new SubTask(splitValue[2], splitValue[4],
                        LocalDateTime.parse(splitValue[5]), Duration.ofMinutes(Integer.parseInt(splitValue[6])),
                        Integer.parseInt(splitValue[7]));
                generatedSubTask.setId(Integer.parseInt(splitValue[0]));
                generatedSubTask.setStatus(TaskStatus.valueOf(splitValue[3]));
                return generatedSubTask;
            case EPIC:
                Epic generatedEpic = new Epic(splitValue[2], splitValue[4]);
                generatedEpic.setId(Integer.parseInt(splitValue[0]));
                generatedEpic.setStatus(TaskStatus.valueOf(splitValue[3]));
                return generatedEpic;
            default:
                System.out.println("Неизвестный тип задачи");
                return null;
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            FileBackedTaskManager loadedManager = new FileBackedTaskManager(file);
            String savedString = Files.readString(file.toPath());

            if (!savedString.contains(SAVE_FILE_HEADER)) {
                System.out.println("Шапка файла сохранения сформирована не корректно");
                return loadedManager;
            }

            savedString = savedString.substring(savedString.indexOf("\n") + 1);
            String[] savedTasks = savedString.split("\n");
            int maxCounter = 0;

            if (savedString.isBlank()) {
                System.out.println("Файл сохранения не содержит сохраненных задач");
                return loadedManager;
            }

            for (String taskString : savedTasks) {
                Task task = loadedManager.fromString(taskString);
                if (task != null) {
                    int taskId = task.getId();
                    if (maxCounter < taskId) maxCounter = taskId;
                    switch (task.getType()) {
                        case TASK:
                            loadedManager.tasks.put(taskId, task);
                            break;
                        case EPIC:
                            loadedManager.epics.put(taskId, (Epic) task);
                            break;
                        case SUBTASK:
                            SubTask subTask = (SubTask) task;
                            int epicId = subTask.getEpicId();
                            loadedManager.subTasks.put(taskId, subTask);
                            Epic epic = loadedManager.epics.get(epicId);
                            epic.addSubTask(taskId);
                    }
                }
            }

            loadedManager.taskCounter = maxCounter + 1;
            return loadedManager;
        } catch (Exception ex) {
            throw new ManagerLoadException("Возникла ошибка при загрузке состояния из файла", ex);
        }
    }
}