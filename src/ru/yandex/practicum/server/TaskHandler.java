package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.exception.HaveOverlapsException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.IOException;

public class TaskHandler extends BaseHandler {
    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestUri = exchange.getRequestURI().toString();
        String method = exchange.getRequestMethod();
        String[] splitUri = requestUri.split("/");
        int taskId = 0;
        if (splitUri.length > 2) taskId = Integer.parseInt(splitUri[2]);

        switch (method) {
            case "GET" -> tasksGet(exchange, taskId);
            case "POST" -> tasksPost(exchange);
            case "DELETE" -> tasksDelete(exchange, taskId);
            default -> sendMethodNotAllowed(exchange);
        }
    }

    void tasksGet(HttpExchange exchange, int taskId) throws IOException {
        if (taskId == 0) {
            sendText(exchange, gson.toJson(manager.getTasks()));
            return;
        }
        try {
            sendText(exchange, gson.toJson(manager.getTaskById(taskId)));
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
            sendNotFound(exchange);
        }
    }

    void tasksPost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        if (body.isBlank()) {
            sendInternalError(exchange);
            return;
        }
        Task task = gson.fromJson(body, Task.class);
        if (task.getStatus() == null) task.setStatus(TaskStatus.NEW);

        try {
            if (task.getId() == 0) {
                manager.createTask(task);
                sendCreated(exchange);
                return;
            }
            manager.updateTask(task);
            sendCreated(exchange);
        } catch (HaveOverlapsException e) {
            System.out.println(e.getMessage());
            sendHasOverlaps(exchange);
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
            sendNotFound(exchange);
        }
    }

    void tasksDelete(HttpExchange exchange, int taskId) throws IOException {
        try {
            manager.deleteTaskById(taskId);
            sendOk(exchange);
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
            sendOk(exchange);
        }
    }
}
