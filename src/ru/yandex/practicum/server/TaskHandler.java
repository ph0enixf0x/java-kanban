package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.IOException;

public class TaskHandler extends BaseHandler implements HttpHandler {
    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestUri = exchange.getRequestURI().toString();
        String method = exchange.getRequestMethod();
        String[] splitUri = requestUri.split("/");
        String endpoint = splitUri[1];

        int taskId = 0;
        if (splitUri.length > 2) taskId = Integer.parseInt(splitUri[2]);

        switch (endpoint) {
            case "tasks":
                switch (method) {
                    case "GET":
                        if (taskId != 0) {
                            try {
                                sendText(exchange, gson.toJson(manager.getTaskById(taskId)));
                            } catch (NotFoundException e) {
                                System.out.println(e.getMessage());
                                sendNotFound(exchange);
                            }
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
                        if (taskId != 0) {
                            try {
                                sendText(exchange, gson.toJson(manager.getSubTaskById(taskId)));
                            } catch (NotFoundException e) {
                                System.out.println(e.getMessage());
                                sendNotFound(exchange);
                            }
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
                        if (taskId != 0) {
                            try {
                                Epic epic = manager.getEpicById(taskId);
                                if (splitUri.length == 4 && splitUri[3].equals("subtasks")) {
                                    sendText(exchange, gson.toJson(manager.getEpicSubTasks(taskId)));
                                    return;
                                }
                                sendText(exchange, gson.toJson(epic));
                            } catch (NotFoundException e) {
                                System.out.println(e.getMessage());
                                sendNotFound(exchange);
                            }
                        } else {
                            sendText(exchange, gson.toJson(manager.getEpics()));
                        }
                        break;
                    case "POST":
                        Epic decodedEpic = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()),
                                Epic.class);
                        int epicId = decodedEpic.getId();

                        Epic epic = new Epic(decodedEpic.getName(), decodedEpic.getDescription());
                        epic.setId(epicId);

                        if (epicId == 0) {
                            epic.setStatus(TaskStatus.NEW);
                            manager.createEpic(epic);
                            sendCreated(exchange);
                            return;
                        }
                        epic.setStatus(manager.getEpicById(epicId).getStatus());
                        manager.getEpicSubTasks(epicId).forEach(subTask -> epic.addSubTask(subTask.getId()));
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
