package api.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import tasks.Task;
import tasks.TaskType;

import java.util.ArrayList;
import java.util.Collection;

public class JsonTaskParser {

    public static Task parseJsonToTask(Gson gson, JsonElement element) {
        TaskType type = gson.fromJson(element.getAsJsonObject().get("type"), TaskType.class);
        return gson.fromJson(element, type.getType());
    }

    public static Collection<Task> parseJsonToTaskCollection(Gson gson, String json) {
        Collection<Task> tasks = new ArrayList<>();
        JsonArray jsonElements = JsonParser.parseString(json).getAsJsonArray();
        for (JsonElement jsonElement : jsonElements) {
            tasks.add(parseJsonToTask(gson, jsonElement));
        }
        return tasks;
    }



}
