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
        try {
            switch (method) {
                case "GET" -> subtasksGet(exchange, taskId);
                case "POST" -> subtasksPost(exchange);
                case "DELETE" -> subtasksDelete(exchange, taskId);
                default -> sendMethodNotAllowed(exchange);
            }
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
            sendNotFound(exchange);
        } catch (HaveOverlapsException e) {
            System.out.println(e.getMessage());
            sendHasOverlaps(exchange);
        }
    }

    private void subtasksGet(HttpExchange exchange, int taskId) throws IOException {
        if (taskId == 0) {
            sendText(exchange, gson.toJson(manager.getSubTasks()));
            return;
        }
        sendText(exchange, gson.toJson(manager.getSubTaskById(taskId)));
    }

    private void subtasksPost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        if (body.isBlank()) {
            sendInternalError(exchange);
            return;
        }
        SubTask subtask = gson.fromJson(body, SubTask.class);
        if (subtask.getStatus() == null) subtask.setStatus(TaskStatus.NEW);

        if (subtask.getId() == 0) {
            manager.createSubTask(subtask);
            sendCreated(exchange);
            return;
        }
        manager.updateSubTask(subtask);
        sendCreated(exchange);
    }

    private void subtasksDelete(HttpExchange exchange, int taskId) throws IOException {
        manager.deleteSubTaskById(taskId);
        sendOk(exchange);
    }
}
