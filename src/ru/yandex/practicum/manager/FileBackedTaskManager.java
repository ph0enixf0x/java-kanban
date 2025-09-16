package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final Path saveFile;

    public FileBackedTaskManager() {
        super();
        this.saveFile = Paths.get("resources", "save.txt");
    }

    public FileBackedTaskManager(Path saveFile) {
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

    private void save() {
        try (FileWriter file = new FileWriter(saveFile.toString())) {
            StringBuilder saveString = new StringBuilder("id,type,name,description,status,info\n");
            for (ArrayList<? extends Task> taskList : List.of(getTasks(), getEpics(), getSubTasks())) {
                for (Task task : taskList) {
                    saveString.append(toString(task)).append("\n");
                }
            }
            file.write(saveString.toString());
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            try {
                throw new ManagerSaveException("Возникла ошибка при работе сохранении состояния в файл", ex);
            } catch (ManagerSaveException e) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public String toString(Task task) {
        String taskString = task.toString();
        taskString = taskString.substring(taskString.indexOf("{") + 1, taskString.length() - 1)
                .replace("'", "")
                .replace("id=", "")
                .replace(" name=", task.getType() + ",")
                .replace(" description=", "")
                .replace(" status=", "");
        if (taskString.contains(" subtasks=[")) {
            String subTasksIds = taskString.substring(taskString.indexOf("[") + 1);
            subTasksIds = subTasksIds
                    .substring(0, subTasksIds.length() - 1)
                    .replace(" ", "");
            taskString = taskString.substring(0, taskString.indexOf(" subtasks=[")) + subTasksIds;
        } else if (taskString.contains(" epicId=")) {
            taskString = taskString.replace(" epicId=", "");
        } else if (taskString.contains("TASK")) {
            taskString = taskString + ",";
        }
        return taskString;
    }

    public Task fromString(String value) {
        String[] splitValue = value.split(",");
        switch (TaskType.valueOf(splitValue[1])) {
            case TASK:
                Task generatedTask = new Task(splitValue[2], splitValue[3]);
                generatedTask.setId(Integer.parseInt(splitValue[0]));
                generatedTask.setStatus(TaskStatus.valueOf(splitValue[4]));
                return generatedTask;
            case SUBTASK:
                SubTask generatedSubTask = new SubTask(splitValue[2], splitValue[3], Integer.parseInt(splitValue[5]));
                generatedSubTask.setId(Integer.parseInt(splitValue[0]));
                generatedSubTask.setStatus(TaskStatus.valueOf(splitValue[4]));
                return generatedSubTask;
            case EPIC:
                Epic generatedEpic = new Epic(splitValue[2], splitValue[3]);
                generatedEpic.setId(Integer.parseInt(splitValue[0]));
                generatedEpic.setStatus(TaskStatus.valueOf(splitValue[4]));
                if (splitValue.length > 5) {
                    for (int i = 5; i < splitValue.length; i++) {
                        generatedEpic.addSubTask(Integer.parseInt(splitValue[i]));
                    }
                }
                return generatedEpic;
            default:
                System.out.println("Неизвестный тип задачи");
                return null;
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            String savedString = Files.readString(file.toPath());
            FileBackedTaskManager loadedManager = new FileBackedTaskManager(file.toPath());

            if (!savedString.contains("id,type,name,description,status,info")) {
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

            for (String task : savedTasks) {
                int taskId = Integer.parseInt(task.substring(0, task.indexOf(",")));
                if (maxCounter < taskId) maxCounter = taskId;
                if (task.contains(",TASK,")) {
                    loadedManager.loadTask(loadedManager.fromString(task));
                } else if (task.contains(",EPIC,")) {
                    loadedManager.loadEpic((Epic) loadedManager.fromString(task));
                } else if (task.contains(",SUBTASK,")) {
                    loadedManager.loadSubTask((SubTask) loadedManager.fromString(task));
                }
            }

            loadedManager.setTaskCounter(maxCounter + 1);
            return loadedManager;
        } catch (IOException ex) {
            try {
                throw new ManagerSaveException("Возникла ошибка при загрузке состояния из файла", ex);
            } catch (ManagerSaveException e) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
                return new FileBackedTaskManager(file.toPath());
            }
        }
    }

    private void setTaskCounter(int counter) {
        this.taskCounter = counter;
    }

    private void loadTask(Task task) {
        tasks.put(task.getId(), task);
    }

    private void loadEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    private void loadSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
    }
}