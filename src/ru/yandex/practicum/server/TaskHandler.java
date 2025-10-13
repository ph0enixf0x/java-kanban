package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseHandler implements HttpHandler {
    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestUri = exchange.getRequestURI().toString();
        String method = exchange.getRequestMethod();
        String[] splitUri = requestUri.split("/");
        int taskId = 0;
        boolean haveId = splitUri.length > 2;
        if (haveId) taskId = Integer.parseInt(splitUri[2]);

        switch (splitUri[1]) {
            case "tasks":
                switch (method) {
                    case "GET":
                        if (haveId) {
                            Task task = manager.getTaskById(taskId);
                            System.out.println(task);
                            if (task == null) {
                                sendNotFound(exchange);
                                return;
                            }
                            sendText(exchange, gson.toJson(task));
                        } else {
                            sendText(exchange, gson.toJson(manager.getTasks()));
                        }
                        break;
                    case "POST":
                        Task decodedTask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()),
                                Task.class);
                        TaskStatus taskStatus = decodedTask.getStatus();

                        Task task = new Task(decodedTask.getName(), decodedTask.getDescription(),
                                decodedTask.getStartTime(), decodedTask.getDuration());
                        task.setId(decodedTask.getId());
                        if (taskStatus != null) {
                            task.setStatus(taskStatus);
                        }

                        if (task.getId() == 0) {
                            if (manager.createTask(task) == 0) {
                                sendHasOverlaps(exchange);
                                return;
                            }
                            sendCreated(exchange);
                        } else {
                            if (isUpdated(exchange, manager.updateTask(task))) {
                                sendCreated(exchange);
                            }
                        }
                        break;
                    case "DELETE":
                        if (manager.deleteTaskById(taskId) == -1) {
                            sendNotFound(exchange);
                            return;
                        }
                        sendOk(exchange);
                        break;
                    default:
                        sendMethodNotAllowed(exchange);
                }
                break;
            case "subtasks":
                switch (method) {
                    case "GET":
                        if (haveId) {
                            SubTask subtask = manager.getSubTaskById(taskId);
                            if (subtask == null) {
                                sendNotFound(exchange);
                                return;
                            }
                            sendText(exchange, gson.toJson(subtask));
                        } else {
                            sendText(exchange, gson.toJson(manager.getSubTasks()));
                        }
                        break;
                    case "POST":
                        SubTask decodedSubtask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()),
                                SubTask.class);
                        TaskStatus subtaskStatus = decodedSubtask.getStatus();

                        SubTask subtask = new SubTask(decodedSubtask.getName(), decodedSubtask.getDescription(),
                                decodedSubtask.getStartTime(), decodedSubtask.getDuration(),
                                decodedSubtask.getEpicId());
                        subtask.setId(decodedSubtask.getId());
                        if (subtaskStatus != null) {
                            subtask.setStatus(subtaskStatus);
                        }

                        if (subtask.getId() == 0) {
                            if (manager.createSubTask(subtask) == 0) {
                                sendHasOverlaps(exchange);
                                return;
                            }
                            sendCreated(exchange);
                        }
                        if (isUpdated(exchange, manager.updateSubTask(subtask))) {
                            sendCreated(exchange);
                        }
                        break;
                    case "DELETE":
                        if (manager.deleteSubTaskById(taskId) == -1) {
                            sendNotFound(exchange);
                            return;
                        }
                        sendOk(exchange);
                        break;
                    default:
                        sendMethodNotAllowed(exchange);
                }
                break;
            case "epics":
                switch (method) {
                    case "GET":
                        if (haveId) {
                            Epic epic = manager.getEpicById(taskId);
                            if (epic == null) {
                                sendNotFound(exchange);
                                return;
                            }
                            if (splitUri.length == 4) {
                                sendText(exchange, gson.toJson(manager.getEpicSubTasks(taskId)));
                                return;
                            }
                            sendText(exchange, gson.toJson(epic));
                        } else {
                            sendText(exchange, gson.toJson(manager.getEpics()));
                        }
                        break;
                    case "POST":
                        Epic decodedEpic = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()),
                                Epic.class);
                        TaskStatus epicStatus = decodedEpic.getStatus();
                        List<Integer> subtaskIds = decodedEpic.getSubtasksIds();

                        Epic epic = new Epic(decodedEpic.getName(), decodedEpic.getDescription());
                        epic.setId(decodedEpic.getId());
                        if (epicStatus != null) {
                            epic.setStatus(epicStatus);
                        }
                        if (subtaskIds != null) {
                            subtaskIds.forEach(epic::addSubTask);
                        }

                        if (epic.getId() == 0) {
                            manager.createEpic(epic);
                            sendCreated(exchange);
                            return;
                        }
                        if (isUpdated(exchange, manager.updateEpic(epic))) {
                            sendCreated(exchange);
                        }
                        break;
                    case "DELETE":
                        if (manager.deleteEpicById(taskId) == -1) {
                            sendNotFound(exchange);
                            return;
                        }
                        sendOk(exchange);
                        break;
                    default:
                        sendMethodNotAllowed(exchange);
                }
        }
    }

    private boolean isUpdated(HttpExchange exchange, int result) throws IOException {
        if (result == -1) {
            sendNotFound(exchange);
            return false;
        } else if (result == 0) {
            sendHasOverlaps(exchange);
            return false;
        }
        return true;
    }
}
