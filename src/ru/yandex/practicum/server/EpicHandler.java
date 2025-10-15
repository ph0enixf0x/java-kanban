package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.Epic;

import java.io.IOException;

public class EpicHandler extends BaseHandler{
    public EpicHandler(TaskManager manager) {
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
            case "GET" -> epicsGet(exchange, taskId,
                    splitUri.length == 4 && splitUri[3].equals("subtasks"));
            case "POST" -> epicsPost(exchange);
            case "DELETE" -> epicsDelete(exchange, taskId);
            default -> sendMethodNotAllowed(exchange);
        }
    }

    void epicsGet(HttpExchange exchange, int taskId, boolean isSubtasksRequest) throws IOException {
        if (taskId == 0) {
            sendText(exchange, gson.toJson(manager.getEpics()));
            return;
        }
        try {
            Epic epic = manager.getEpicById(taskId);
            if (isSubtasksRequest) {
                sendText(exchange, gson.toJson(manager.getEpicSubTasks(taskId)));
                return;
            }
            sendText(exchange, gson.toJson(epic));
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
            sendNotFound(exchange);
        }
    }

    void epicsPost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        if (body.isBlank()) {
            sendInternalError(exchange);
            return;
        }
        Epic epic = gson.fromJson(body, Epic.class);
        manager.createEpic(epic);
        sendCreated(exchange);
    }

    void epicsDelete(HttpExchange exchange, int taskId) throws IOException {
        try {
            manager.deleteEpicById(taskId);
            sendOk(exchange);
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
            sendOk(exchange);
        }
    }
}
