package api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.tasks.TaskManager;

import java.io.IOException;

import static api.Constants.GET;

public class EpicTaskHandler extends AbstractTaskHandler {


    public EpicTaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected String methodsHandler(HttpExchange exchange) throws IOException {
        String response = "";
        switch (exchange.getRequestMethod()) {
            case GET:
                response = getAllEpics();
                break;
            default:
                badMethodStatusCode(exchange);
        }
        return response;
    }

    private String getAllEpics() {
        return gson.toJson(taskManager.getAllEpics());
    }
}
