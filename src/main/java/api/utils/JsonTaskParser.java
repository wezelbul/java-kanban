package api.utils;

import api.adapters.DurationAdapter;
import api.adapters.LocalDateTimeAdapter;
import com.google.gson.*;
import tasks.Task;
import tasks.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class JsonTaskParser {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public static Task parseJsonToTask(JsonElement element) {
        TaskType type = GSON.fromJson(element.getAsJsonObject().get("type"), TaskType.class);
        return GSON.fromJson(element, type.getType());
    }

    public static Collection<Task> parseJsonToTaskCollection(String json) {
        Collection<Task> tasks = new ArrayList<>();
        JsonArray jsonElements = JsonParser.parseString(json).getAsJsonArray();
        for (JsonElement jsonElement : jsonElements) {
            tasks.add(parseJsonToTask(jsonElement));
        }
        return tasks;
    }



}
