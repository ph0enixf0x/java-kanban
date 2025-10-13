package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    public static void main(String[] args) {
        try {
            HttpServer server = start();
            TaskManager manager = new Managers().getDefault();
            TaskHandler taskHandler = new TaskHandler(manager);
            UserHandler userHandler = new UserHandler(manager);

            server.createContext("/tasks", taskHandler);
            server.createContext("/subtasks", taskHandler);
            server.createContext("/epics", taskHandler);
            server.createContext("/history", userHandler);
            server.createContext("/prioritized", userHandler);


        } catch (Exception e) {
            System.out.println("Что то пошло не так при работе сервера!");
            e.getStackTrace();
        }
    }

    public static HttpServer start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.start();
        System.out.println("Сервер менеджера задач запущен на порте 8080");
        return server;
    }

    public static void stop(HttpServer server) {
        server.stop(5);
        System.out.println("Сервер менеджера задач остановлен");
    }
}
