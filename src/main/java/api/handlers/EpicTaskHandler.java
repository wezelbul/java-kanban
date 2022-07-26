package api.handlers;

import api.utils.JsonTaskParser;
import com.sun.net.httpserver.HttpExchange;
import managers.tasks.TaskManager;

import java.io.IOException;

import static api.Constants.GET;

public class EpicTaskHandler extends AbstractTaskHandler {


    public EpicTaskHandler(TaskManager taskManager) {
        super(taskManager);
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
        return JsonTaskParser.GSON.toJson(taskManager.getAllEpics());
    }
}
