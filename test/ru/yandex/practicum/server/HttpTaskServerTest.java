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
    TaskManager manager;

    @BeforeAll
    static void beforeAll() {
        client = HttpClient.newHttpClient();
        host = "http://localhost:8080/";
    }

    @BeforeEach
    void beforeEach() throws IOException {
        server = HttpTaskServer.start();
        manager = new Managers().getDefault();
        UserHandler userHandler = new UserHandler(manager);

        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
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
        String taskJson = """
                {
                \t"name": "Первая задача",
                \t"description": "Описание первой задачи",
                \t"startTime": "2025-10-12T14:45:22",
                \t"duration": 60
                }""";
        String updatedTaskJson = """
                {
                \t"id": 1,
                \t"name": "Первая задача с новым названием",
                \t"description": "Новое описание первой задачи",
                \t"status": "IN_PROGRESS",
                \t"startTime": "2025-10-13T14:45:22",
                \t"duration": 120
                }""";

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
                "Код ответа при попытке создания пересекающейся задачи отличается от ожидаемого");

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
                "Код ответа при получении задачи по идентификатору отличается от ожидаемого");
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
                "Код ответа при удалении задачи отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + "tasks/1"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(),
                "Код ответа при запросе несуществующей задачи отличается от ожидаемого");
    }

    @Test
    void checkEpicsEndpoint() throws IOException, InterruptedException {
        String epicJson = """
                {
                \t"name": "Эпик один",
                \t"description": "Первый Эпик"
                }""";
        String subtaskJson = """
                {
                \t"epicId": 1,
                \t"name": "Подзадача один",
                \t"description": "Первая подзадача первого эпика",
                \t"startTime": "2025-10-16T16:24:59",
                \t"duration": 120
                }""";

        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .uri(URI.create(host + "epics"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(),
                "Код ответа при создании нового эпика отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + "epics"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),
                "Код ответа при получении созданных эпиков отличается от ожидаемого");
        assertEquals("[{" +
                        "\"subtasksIds\":[]," +
                        "\"id\":1," +
                        "\"name\":\"Эпик один\"," +
                        "\"description\":\"Первый Эпик\"," +
                        "\"status\":\"NEW\"," +
                        "\"duration\":0" +
                        "}]", response.body(),
                "Возвращенный эпик отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .uri(URI.create(host + "subtasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(),
                "Код ответа при создании новой подзадачи отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + "epics/1"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),
                "Код ответа при получении эпика по идентификатору отличается от ожидаемого");
        assertEquals("{" +
                        "\"subtasksIds\":[2]," +
                        "\"endTime\":\"2025-10-16T18:24:59\"," +
                        "\"id\":1," +
                        "\"name\":\"Эпик один\"," +
                        "\"description\":\"Первый Эпик\"," +
                        "\"status\":\"NEW\"," +
                        "\"startTime\":\"2025-10-16T16:24:59\"," +
                        "\"duration\":120}", response.body(),
                "Возвращенный обновленный эпик отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + "epics/1/subtasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),
                "Код ответа при получении списка подзадач эпика отличается от ожидаемого");
        assertEquals("[{" +
                        "\"epicId\":1," +
                        "\"id\":2," +
                        "\"name\":\"Подзадача один\"," +
                        "\"description\":\"Первая подзадача первого эпика\"," +
                        "\"status\":\"NEW\"," +
                        "\"startTime\":\"2025-10-16T16:24:59\"," +
                        "\"duration\":120}" +
                        "]", response.body(),
                "Возвращенный список подзадач эпика отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(host + "epics/1"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),
                "Код ответа при удалении эпика отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + "epics/1"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(),
                "Код ответа при запросе несуществующего эпика отличается от ожидаемого");
    }

    @Test
    void checkSubtasksEndpoint() throws IOException, InterruptedException {
        String epicJson = """
                {
                \t"name": "Эпик один",
                \t"description": "Первый Эпик"
                }""";
        String subtaskJson = """
                {
                \t"epicId": 1,
                \t"name": "Подзадача один",
                \t"description": "Первая подзадача первого эпика",
                \t"startTime": "2025-10-16T16:24:59",
                \t"duration": 120
                }""";
        String updatedSubtaskJson = """
                {
                \t"id": 2,
                \t"epicId": 1,
                \t"name": "Новое название первой подзадачи",
                \t"description": "Новое описание первой подзадачи",
                \t"status": "IN_PROGRESS",
                \t"startTime": "2025-10-17T16:24:59",
                \t"duration": 60
                }""";

        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .uri(URI.create(host + "epics"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(),
                "Код ответа при создании нового эпика отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .uri(URI.create(host + "subtasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(),
                "Код ответа при создании новой подзадачи отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .uri(URI.create(host + "subtasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(),
                "Код ответа при попытке создания пересекающейся подзадачи отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + "subtasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),
                "Код ответа при получении созданных подзадач отличается от ожидаемого");
        assertEquals("[{" +
                        "\"epicId\":1," +
                        "\"id\":2," +
                        "\"name\":\"Подзадача один\"," +
                        "\"description\":\"Первая подзадача первого эпика\"," +
                        "\"status\":\"NEW\"," +
                        "\"startTime\":\"2025-10-16T16:24:59\"," +
                        "\"duration\":120}" +
                        "]", response.body(),
                "Возвращенная подзадача отличается от ожидаемой");

        response = client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson))
                .uri(URI.create(host + "subtasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(),
                "Код ответа при обновлении подзадачи отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + "subtasks/2"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),
                "Код ответа при получении подзадачи по идентификатору отличается от ожидаемого");
        assertEquals("{" +
                        "\"epicId\":1," +
                        "\"id\":2," +
                        "\"name\":\"Новое название первой подзадачи\"," +
                        "\"description\":\"Новое описание первой подзадачи\"," +
                        "\"status\":\"IN_PROGRESS\"," +
                        "\"startTime\":\"2025-10-17T16:24:59\"," +
                        "\"duration\":60" +
                        "}", response.body(),
                "Возвращенная подзадача отличается от ожидаемой");

        response = client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(host + "subtasks/2"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),
                "Код ответа при удалении подзадачи отличается от ожидаемого");

        response = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + "subtasks/2"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(),
                "Код ответа при получении несуществующей подзадачи отличается от ожидаемого");
    }

    @Test
    void checkUserEndpoints() throws IOException, InterruptedException  {
        String task1Json = """
                {
                \t"name": "Первая задача",
                \t"description": "Описание первой задачи",
                \t"startTime": "2025-10-12T14:45:22",
                \t"duration": 60
                }""";
        String task2Json = """
                {
                \t"name": "Первая задача",
                \t"description": "Описание первой задачи",
                \t"startTime": "2025-10-13T14:45:22",
                \t"duration": 60
                }""";

        client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .uri(URI.create(host + "tasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());

        client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .uri(URI.create(host + "tasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());

        client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + "tasks/2"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());

        client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + "tasks/1"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + "history"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),
                "Код ответа при получении задачи по идентификатору отличается от ожидаемого");
        assertEquals("[" +
                        "{\"id\":2," +
                        "\"name\":\"Первая задача\"," +
                        "\"description\":\"Описание первой задачи\"," +
                        "\"status\":\"NEW\"," +
                        "\"startTime\":\"2025-10-13T14:45:22\"," +
                        "\"duration\":60}," +
                        "{\"id\":1," +
                        "\"name\":\"Первая задача\"," +
                        "\"description\":\"Описание первой задачи\"," +
                        "\"status\":\"NEW\"," +
                        "\"startTime\":\"2025-10-12T14:45:22\"," +
                        "\"duration\":60}" +
                        "]", response.body(),
                "Возвращенная история отличается от ожидаемой");

        response = client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(host + "prioritized"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),
                "Код ответа при получении задачи по идентификатору отличается от ожидаемого");
        assertEquals("[" +
                        "{\"id\":1," +
                        "\"name\":\"Первая задача\"," +
                        "\"description\":\"Описание первой задачи\"," +
                        "\"status\":\"NEW\"," +
                        "\"startTime\":\"2025-10-12T14:45:22\"," +
                        "\"duration\":60}," +
                        "{\"id\":2," +
                        "\"name\":\"Первая задача\"," +
                        "\"description\":\"Описание первой задачи\"," +
                        "\"status\":\"NEW\"," +
                        "\"startTime\":\"2025-10-13T14:45:22\"," +
                        "\"duration\":60}" +
                        "]", response.body(),
                "Возвращенная история отличается от ожидаемой");
    }
}