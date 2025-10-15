package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.exception.HaveOverlapsException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.IOException;

public class SubtaskHandler extends  BaseHandler {
    public SubtaskHandler(TaskManager manager) {
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
            case "GET" -> subtasksGet(exchange, taskId);
            case "POST" -> subtasksPost(exchange);
            case "DELETE" -> subtasksDelete(exchange, taskId);
            default -> sendMethodNotAllowed(exchange);
        }
    }

    void subtasksGet(HttpExchange exchange, int taskId) throws IOException {
        if (taskId == 0) {
            sendText(exchange, gson.toJson(manager.getSubTasks()));
            return;
        }
        try {
            sendText(exchange, gson.toJson(manager.getSubTaskById(taskId)));
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
            sendNotFound(exchange);
        }
    }

    void subtasksPost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        if (body.isBlank()) {
            sendInternalError(exchange);
            return;
        }
        SubTask subtask = gson.fromJson(body, SubTask.class);
        if (subtask.getStatus() == null) subtask.setStatus(TaskStatus.NEW);

        try {
            if (subtask.getId() == 0) {
                manager.createSubTask(subtask);
                sendCreated(exchange);
                return;
            }
            manager.updateSubTask(subtask);
            sendCreated(exchange);
        } catch (HaveOverlapsException e) {
            System.out.println(e.getMessage());
            sendHasOverlaps(exchange);
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
            sendNotFound(exchange);
        }
    }

    void subtasksDelete(HttpExchange exchange, int taskId) throws IOException {
        try {
            manager.deleteSubTaskById(taskId);
            sendOk(exchange);
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
            sendOk(exchange);
        }
    }
}
