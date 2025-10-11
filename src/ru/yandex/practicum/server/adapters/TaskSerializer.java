package ru.yandex.practicum.server.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ru.yandex.practicum.tasks.Task;

import java.lang.reflect.Type;
import java.time.temporal.ChronoUnit;

public class TaskSerializer implements JsonSerializer<Task> {

    @Override
    public JsonElement serialize(Task task, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();

        result.addProperty("id", task.getId());
        result.addProperty("type", "TASK");
        result.addProperty("name", task.getName());
        result.addProperty("status", task.getStatus().toString());
        result.addProperty("description", task.getDescription());
        result.addProperty("startTime", task.getStartTime().truncatedTo(ChronoUnit.SECONDS).toString());
        result.addProperty("duration", task.getDuration().toMinutes());

        return  result;
    }
}
