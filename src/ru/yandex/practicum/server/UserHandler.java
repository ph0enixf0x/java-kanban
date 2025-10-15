package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.manager.TaskManager;

import java.io.IOException;

public class UserHandler extends BaseHandler {
    public UserHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestUri = exchange.getRequestURI().toString();
        String method = exchange.getRequestMethod();
        String[] splitUri = requestUri.split("/");

        switch (splitUri[1]) {
            case "history":
                if (method.equals("GET")) {
                    sendText(exchange, gson.toJson(manager.getHistory()));
                } else {
                    sendMethodNotAllowed(exchange);
                }
                break;
            case "prioritized":
                if (method.equals("GET")) {
                    sendText(exchange, gson.toJson(manager.getPrioritizedTasks()));
                } else {
                    sendMethodNotAllowed(exchange);
                }
        }
    }
}
