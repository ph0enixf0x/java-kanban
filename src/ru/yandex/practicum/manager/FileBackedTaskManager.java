package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager{

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
        }
        return taskString;
    }
}
