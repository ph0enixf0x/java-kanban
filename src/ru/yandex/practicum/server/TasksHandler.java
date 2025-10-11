package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.server.adapters.DurationAdapter;
import ru.yandex.practicum.server.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class TasksHandler extends BaseHandler implements HttpHandler {
    public TasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        String requestUri = exchange.getRequestURI().toString();
        String method = exchange.getRequestMethod();
        String[] splitUri = requestUri.split("/");
        int taskId = 0;
        boolean haveId = splitUri.length == 3;
        if (haveId) taskId = Integer.parseInt(splitUri[2]);

        switch (splitUri[1]) {
            case "tasks":
                switch (method) {
                    case "GET":
                        if (haveId) {
                            Task task = manager.getTaskById(taskId);
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
                        Task task = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()),
                                Task.class);
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
            case "/subtasks":
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
                        SubTask subtask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()),
                                SubTask.class);
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
