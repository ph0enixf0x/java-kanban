package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.server.adapters.DurationAdapter;
import ru.yandex.practicum.server.adapters.LocalDateTimeAdapter;
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
        if (requestUri.contains("/tasks")) {
            int taskId = 0;
            if (requestUri.contains("tasks/")) {
                taskId = Integer.parseInt(
                        requestUri.substring(requestUri.lastIndexOf("/") + 1));
            }
            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (requestUri.contains("tasks/")) {
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
                    Task task = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), Task.class);
                    if (task.getId() == 0) {
                        if (manager.createTask(task) == 0) {
                            sendHasOverlaps(exchange);
                            return;
                        }
                        sendCreated(exchange);
                    } else {
                        int result = manager.updateTask(task);
                        if (result == -1) {
                            sendNotFound(exchange);
                            return;
                        } else if (result == 0) {
                            sendHasOverlaps(exchange);
                            return;
                        }
                        sendCreated(exchange);
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
        }


    }
}
