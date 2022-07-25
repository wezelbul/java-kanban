package api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.tasks.TaskManager;

import java.io.IOException;

import static api.Constants.*;

public class HistoryTaskHandler extends AbstractTaskHandler {

    public HistoryTaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected String methodsHandler(HttpExchange exchange) throws IOException {
        String response = "";
        switch (exchange.getRequestMethod()) {
            case GET:
                response = getHistory();
                break;
            default:
                badMethodStatusCode(exchange);
        }
        return response;
    }

    private String getHistory() {
        return gson.toJson(taskManager.history());
    }

}
