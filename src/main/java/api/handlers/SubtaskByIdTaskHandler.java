package api.handlers;

import api.utils.JsonTaskParser;
import com.sun.net.httpserver.HttpExchange;
import managers.tasks.TaskManager;

import java.io.IOException;
import java.util.Map;

import static api.Constants.GET;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

public class SubtaskByIdTaskHandler extends AbstractTaskHandler {

    public SubtaskByIdTaskHandler(TaskManager taskManager) {
        super(taskManager);
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
                        return JsonTaskParser.GSON.toJson("there is no epic's id");
                    }
                    return JsonTaskParser.GSON.toJson(taskManager.getSubtasksById(id));
                } else {
                    statusCode = HTTP_NOT_FOUND;
                    return JsonTaskParser.GSON.toJson("no data");
                }
            } else {
                statusCode = HTTP_BAD_REQUEST;
                return JsonTaskParser.GSON.toJson("incorrect id or field name");
            }
        } else {
            statusCode = HTTP_BAD_REQUEST;
            return JsonTaskParser.GSON.toJson(taskManager.getAllTasks());
        }
    }
}
