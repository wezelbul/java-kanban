package api.servers;

import api.adapters.LocalDateTimeAdapter;
import api.adapters.DurationAdapter;
import api.handlers.*;
import managers.tasks.TaskManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import java.time.Duration;
import java.time.LocalDateTime;

import static api.Constants.*;

public class HttpTaskServer {

    private HttpServer httpServer;
    private Gson gson;
    private TaskManager taskManager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.taskManager = manager;

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        httpServer = HttpServer.create(new InetSocketAddress(TASK_SERVER_PORT), DEFAULT_BACKLOG);
        httpServer.createContext("/tasks/task/", new TaskHandler(taskManager, gson));
        httpServer.createContext("/tasks/subtask/epic/", new SubtaskByIdTaskHandler(taskManager, gson));
        httpServer.createContext("/tasks/epic/", new EpicTaskHandler(taskManager, gson));
        httpServer.createContext("/tasks/history/", new HistoryTaskHandler(taskManager, gson));
        httpServer.createContext("/tasks/", new PrioritizedTaskHandler(taskManager, gson));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(DEFAULT_DELAY);
    }

}
