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

            server.createContext("/tasks", new TasksHandler(manager));
            server.createContext("/subtasks", new SubtasksHandler(manager));
            server.createContext("/epics", new EpicsHandler(manager));
            server.createContext("/history", new HistoryHandler(manager));
            server.createContext("/prioritized", new PrioritizedHandler(manager));


        } catch (IOException e) {
            System.out.println("Что то пошло не так при работе сервера!");
            e.getStackTrace();
        }
    }

    public static HttpServer start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.start();
        return server;
    }

    public static void stop(HttpServer server) {
        server.stop(5);
    }
}
