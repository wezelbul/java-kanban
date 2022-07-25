package api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.tasks.HttpTaskManager;
import managers.tasks.TaskManager;

import java.io.IOException;
import java.util.Map;

import static api.Constants.GET;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

public class SubtaskByIdTaskHandler extends AbstractTaskHandler {

    public SubtaskByIdTaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected String methodsHandler(HttpExchange exchange) throws IOException {
        String response = "";
        switch (exchange.getRequestMethod()) {
            case GET:
                response = getSubtasksByEpicId(exchange);
                break;
            default:
                badMethodStatusCode(exchange);
        }
        return response;
    }

    private String getSubtasksByEpicId(HttpExchange exchange) {
        String rawQuery = exchange.getRequestURI().getRawQuery();
        if (rawQuery != null) {
            Map<String, String> queries = getMapFromRawQuery(rawQuery);
            Integer id = getIdFromQuery(queries.get(FIELD_ID));
            if (id != null && id > 0) {
                if (taskManager.getSubtasksById(id) != null) {
                    if (taskManager.getSubtasksById(id).isEmpty()) {
                        return gson.toJson("there is no epic's id");
                    }
                    return gson.toJson(taskManager.getSubtasksById(id));
                } else {
                    statusCode = HTTP_NOT_FOUND;
                    return gson.toJson("no data");
                }
            } else {
                statusCode = HTTP_BAD_REQUEST;
                return gson.toJson("incorrect id or field name");
            }
        } else {
            statusCode = HTTP_BAD_REQUEST;
            return gson.toJson(taskManager.getAllTasks());
        }
    }
}
