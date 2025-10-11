package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    public static void main(String[] args) {
        try {
            HttpServer server = start();

            
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
