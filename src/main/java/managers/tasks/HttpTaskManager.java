package managers.tasks;

import api.adapters.DurationAdapter;
import api.adapters.LocalDateTimeAdapter;
import api.clients.KVTaskClient;
import api.utils.JsonTaskParser;
import com.google.gson.*;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class HttpTaskManager extends FileBackedTasksManager {

    private static final String TASKS = "tasks";
    private static final String HISTORY = "history";

    private KVTaskClient client;
    private Gson gson;

    public HttpTaskManager(String url) {
        idCounter = 0;
        this.client = new KVTaskClient(url);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @Override
    public void save() {
        client.put(TASKS, gson.toJson(super.getAllTasks()));
        client.put(HISTORY, gson.toJson(historyToString(historyManager)));
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = super.getTaskById(id);
        if (task != null) {
            historyManager.add(task);
            save();
        }
        return task;
    }

    public void loadFromKvServer() {
        Collection<Task> tasks = JsonTaskParser.parseJsonToTaskCollection(gson, client.load(TASKS));
        String stringHistoryId = gson.fromJson(client.load(HISTORY), String.class);
        ArrayList<Integer> historyIdTasks = (ArrayList<Integer>) historyIdFromString(stringHistoryId);
        for (Task task : tasks) {
            updateTaskById(task, task.getId());
        }
        for (int id : historyIdTasks) {
            Task task = taskMap.get(id);
            historyManager.add(task);
        }
    }
}
