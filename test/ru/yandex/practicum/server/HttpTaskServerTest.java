package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    HttpServer server;
    static HttpClient client;
    static String host;

    @BeforeAll
    static void beforeAll() {
        client = HttpClient.newHttpClient();
        host = "http://localhost:8080/";
    }

    @BeforeEach
    void beforeEach() throws IOException {
        server = HttpTaskServer.start();
        TaskManager manager = new Managers().getDefault();
        TaskHandler taskHandler = new TaskHandler(manager);
        UserHandler userHandler = new UserHandler(manager);

        server.createContext("/tasks", taskHandler);
        server.createContext("/subtasks", taskHandler);
        server.createContext("/epics", taskHandler);
        server.createContext("/history", userHandler);
        server.createContext("/prioritized", userHandler);
    }

    @AfterEach
    void afterEach() {
        HttpTaskServer.stop(server, 0);
    }

    @AfterAll
    static void afterAll() {
        client.close();
    }

    @Test
    void checkTasksEndpoint() throws IOException, InterruptedException {
        String taskJson = "{\n" +
                "\t\"name\": \"Первая задача\",\n" +
                "\t\"description\": \"Описание первой задачи\",\n" +
                "\t\"startTime\": \"2025-10-12T14:45:22\",\n" +
                "\t\"duration\": 60\n" +
                "}";
        String updatedTaskJson = "{\n" +
                "\t\"id\": 1,\n" +
                "\t\"name\": \"Первая задача с новым названием\",\n" +
                "\t\"description\": \"Новое описание первой задачи\",\n" +
                "\t\"status\": \"IN_PROGRESS\",\n" +
                "\t\"startTime\": \"2025-10-13T14:45:22\",\n" +
                "\t\"duration\": 120\n" +
                "}";

        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(URI.create(host + "tasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(),
                "Код ответа при создании новой задачи отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(URI.create(host + "tasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(),
                "Код ответа при создании новой задачи отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + "tasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),
                "Код ответа при получении созданных задач отличается от ожидаемого");
        assertEquals("[{" +
                        "\"id\":1," +
                        "\"name\":\"Первая задача\"," +
                        "\"description\":\"Описание первой задачи\"," +
                        "\"status\":\"NEW\"," +
                        "\"startTime\":\"2025-10-12T14:45:22\"," +
                        "\"duration\":60}" +
                        "]", response.body(),
                "Возвращенная задача отличается от ожидаемой");

        response = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .uri(URI.create(host + "tasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(),
                "Код ответа при обновлении задачи отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + "tasks/1"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),
                "Код ответа при получении задачи отличается от ожидаемого");
        assertEquals("{" +
                "\"id\":1," +
                "\"name\":\"Первая задача с новым названием\"," +
                "\"description\":\"Новое описание первой задачи\"," +
                "\"status\":\"IN_PROGRESS\"," +
                "\"startTime\":\"2025-10-13T14:45:22\"," +
                "\"duration\":120" +
                "}", response.body(),
                "Возвращенная обновленная задача отличается от ожидаемой");

        response = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(host + "tasks/1"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),
                "Код ответа при получении задачи отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + "tasks/1"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(),
                "Код ответа при получении задачи отличается от ожидаемого");
    }
}