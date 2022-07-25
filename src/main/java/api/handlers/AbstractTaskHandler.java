package api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.tasks.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static api.Constants.*;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_OK;

public abstract class AbstractTaskHandler implements HttpHandler {

    protected TaskManager taskManager;
    protected Gson gson;

    protected int statusCode = HTTP_OK;
    protected static final String FIELD_ID = "id";

    public AbstractTaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = methodsHandler(exchange);
        resultResponse(exchange, response, statusCode);
        resetStatusCode();
    }

    protected abstract String methodsHandler(HttpExchange exchange) throws IOException;

    protected void resetStatusCode() {
        statusCode = HTTP_OK;
    }

    protected void resultResponse(HttpExchange exchange, String response, int statusCode) {
        try (OutputStream stream = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(statusCode, DEFAULT_RESPONSE_LENGTH);
            stream.write(response.getBytes(STANDARD_CHARSET));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Integer getIdFromQuery(String query) {
        if (query != null) {
            return Integer.parseInt(query);
        } else {
            return null;
        }
    }

    protected void badMethodStatusCode(HttpExchange exchange) {
        System.out.println("Неподдерживаемый запрос: " + exchange.getRequestMethod());
        statusCode = HTTP_BAD_METHOD;
    }

    protected Map<String, String> getMapFromRawQuery(String rawQuery) {
        Map<String, String> result = new HashMap<>();
        for (String query : rawQuery.split("&")) {
            try {
                String[] keyValue = query.split("=");
                result.put(keyValue[0], keyValue[1]);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                String[] keyValue = query.split("=");
                result.put(keyValue[0], null);
            }
        }
        return result;
    }

}
