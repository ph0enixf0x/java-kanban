package ru.yandex.practicum.server.adapters;

import com.google.gson.*;
import ru.yandex.practicum.tasks.Epic;

import java.lang.reflect.Type;

public class EpicDeserializer implements JsonDeserializer<Epic> {
    @Override
    public Epic deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        return new Epic(jsonObject.get("name").getAsString(),
                jsonObject.get("description").getAsString());
    }
}
