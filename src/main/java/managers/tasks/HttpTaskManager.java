package managers.tasks;

import api.clients.KVTaskClient;
import api.utils.JsonTaskParser;
import tasks.Task;

import java.util.ArrayList;
import java.util.Collection;

public class HttpTaskManager extends FileBackedTasksManager {

    private static final String TASKS = "tasks";
    private static final String HISTORY = "history";

    private KVTaskClient client;


    public HttpTaskManager(String url) {
        idCounter = 0;
        this.client = new KVTaskClient(url);
    }

    @Override
    public void save() {
        client.put(TASKS, JsonTaskParser.GSON.toJson(super.getAllTasks()));
        client.put(HISTORY, JsonTaskParser.GSON.toJson(historyToString(historyManager)));
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
        Collection<Task> tasks = JsonTaskParser.parseJsonToTaskCollection(client.load(TASKS));
        String stringHistoryId = JsonTaskParser.GSON.fromJson(client.load(HISTORY), String.class);
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
