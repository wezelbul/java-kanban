package api.handlers;

import api.utils.JsonTaskParser;
import managers.tasks.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;

import static api.Constants.*;
import static java.net.HttpURLConnection.*;

public class TaskHandler extends AbstractTaskHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected String methodsHandler(HttpExchange exchange) throws IOException {
        String response = "";
        switch (exchange.getRequestMethod()) {
            case GET:
                response = getTask(exchange);
                break;
            case POST:
                postTask(exchange);
                break;
            case DELETE:
                deleteTask(exchange);
                break;
            default:
                badMethodStatusCode(exchange);
        }
        return response;
    }

    private String getTask(HttpExchange exchange) {
        String rawQuery = exchange.getRequestURI().getRawQuery();
        if (rawQuery != null) {
            Map<String, String> queries = getMapFromRawQuery(rawQuery);
            Integer id = getIdFromQuery(queries.get(FIELD_ID));
            if (id != null && id > 0) {
                if (taskManager.getTaskById(id) != null) {
                    return JsonTaskParser.GSON.toJson(taskManager.getTaskById(id));
                } else {
                    statusCode = HTTP_NOT_FOUND;
                    return JsonTaskParser.GSON.toJson("no data");
                }
            }
            else {
                statusCode = HTTP_BAD_REQUEST;
                return JsonTaskParser.GSON.toJson("incorrect id or field name");
            }
        } else {
            return JsonTaskParser.GSON.toJson(taskManager.getAllTasks());
        }
    }

    private void postTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        JsonElement element = JsonParser.parseString(
                new String(inputStream.readAllBytes(), STANDARD_CHARSET));
        Task postedTask = JsonTaskParser.parseJsonToTask(element);

        if (
                (postedTask.getId() == null && taskManager.addTask(postedTask))
                || (postedTask.getId() != null && taskManager.updateTaskById(postedTask, postedTask.getId()))
        ) {
            statusCode = HTTP_CREATED;
        } else {
            statusCode = HTTP_BAD_REQUEST;
        }
    }

    private void deleteTask(HttpExchange exchange) {
        String rawQuery = exchange.getRequestURI().getRawQuery();
        if (rawQuery != null) {
            Map<String, String> queries = getMapFromRawQuery(rawQuery);
            Integer id = getIdFromQuery(queries.get(FIELD_ID));
            if (id != null) {
                taskManager.deleteTask(id);
            } else {
                statusCode = HTTP_BAD_REQUEST;
            }
        } else {
                taskManager.deleteTask();
        }
    }
}
