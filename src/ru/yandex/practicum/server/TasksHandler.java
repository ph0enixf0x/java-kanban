package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;

public class TasksHandler extends BaseHandler implements HttpHandler {
    public TasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskSerialiser())
                .create();
        switch (exchange.getRequestMethod()) {
            case "GET":
                String requestUri = exchange.getRequestURI().toString();
                if (requestUri.contains("tasks/")) {
                    Task task = manager.getTaskById(Integer.parseInt(
                            requestUri.substring(requestUri.lastIndexOf("/") + 1)));
                    if (task == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    sendText(exchange, gson.toJson(task));
                } else {
                    sendText(exchange, gson.toJson(manager.getTasks()));
                }
                break;
        }
    }
}
