package managers.tasks.http;

import api.adapters.DurationAdapter;
import api.adapters.LocalDateTimeAdapter;
import api.servers.HttpTaskServer;
import api.servers.KVServer;
import api.utils.JsonTaskParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import managers.tasks.BaseManagerTest;
import managers.tasks.HttpTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import tasks.TaskStatus;
import tasks.TaskType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;


public class RequestsTest extends BaseManagerTest<HttpTaskManager> {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    HttpTaskServer httpTaskServer;
    KVServer kvServer;

    HttpClient httpClient;
    HttpRequest request;
    final String TASK = "/tasks/task/";
    final String BY_ID = "?id=";
    final String SUBTASK_BY_EPIC = "/tasks/subtask/epic/";
    final String EPIC = "/tasks/epic/";
    final String PRIORITIZED_TASKS = "/tasks/";
    final String HISTORY = "/tasks/history/";

    @Override
    @BeforeEach
    public void createManagerForTest() throws IOException {
        httpClient = HttpClient.newHttpClient();

        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HttpTaskManager(kvServer.getUrl());
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
        createTestTasks();
        createTestHistory();
    }

    @Override
    @AfterEach
    protected void clear() {
        taskManager.deleteTask();
        kvServer.stop();
        httpTaskServer.stop();
        httpClient = null;
    }

    @Test
    void getAllTasksRequestTest() {
        testTaskCollectionWithServerResponse(taskManager.getAllTasks(), TASK);
    }

    @Test
    void getAllEpicsRequestTest() {
        testTaskCollectionWithServerResponse(taskManager.getAllEpics(), EPIC);
    }

    @Test
    void getPrioritizedTasksRequestTest() {
        taskManager.getTaskById(4)
                .setStartTime("07:00 19.01.2021");
        taskManager.getTaskById(5)
                .setStartTime("07:10 19.01.2021");
        taskManager.getTaskById(6)
                .setStartTime("07:30 19.01.2021");
        taskManager.getTaskById(12)
                .setStartTime("07:15 19.01.2021");
        taskManager.getTaskById(11)
                .setStartTime("11:00 19.01.2021");
        taskManager.getTaskById(10)
                .setStartTime("10:00 19.01.2021");
        taskManager.updateEpicStartTimeAndDuration(4);
        taskManager.updateEpicStartTimeAndDuration(5);
        taskManager.updateEpicStartTimeAndDuration(6);

        testTaskCollectionWithServerResponse(taskManager.getPrioritizedTasks(), PRIORITIZED_TASKS);
    }

    @Test
    void getHistoryTasksRequestTest() {
        testTaskCollectionWithServerResponse(taskManager.history(), HISTORY);
    }

    @Test
    void getTaskByIdRequestTest() {
        for (Task onServer : taskManager.getAllTasks()) {
            HttpResponse<String> response = getResponseFromGetRequest(TASK + BY_ID + onServer.getId());

            Task fromServer = JsonTaskParser.parseJsonToTask(
                    gson,
                    JsonParser.parseString(response.body()));

            assertEquals(onServer, fromServer, "Задачи не совпадают");
        }
    }

    @Test
    void getSubtasksByEpicIdRequestTest() {
        for (Task onServer : taskManager.getAllTasks()) {
            if (onServer.getType().equals(TaskType.EPIC)) {
                testTaskCollectionWithServerResponse(taskManager.getSubtasksById(onServer.getId()),
                        SUBTASK_BY_EPIC + BY_ID + onServer.getId());
            }
        }
    }

    @Test
    void deleteAllTasksRequestTest() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(HTTP_PROTOCOL + HOSTNAME + ':' + TASK_SERVER_PORT + TASK))
                .version(CLIENT_VERSION)
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertNull(taskManager.getAllTasks(), "Список задач не пуст");
        assertNull(taskManager.history(), "История вызовов не пуста");
        Task task2 = new Task("Купить хлеба",
                "Купить хлеба по пути из школы",
                TaskStatus.DONE);
        taskManager.addTask(task2);
        task2.setId(1);
        assertEquals(task2, taskManager.getTaskById(1), "Задачи не совпадают");
    }

    @Test
    void deleteTaskByIdRequestTest() throws IOException, InterruptedException {
        int taskId = 4;
        Set<Integer> subtasks;
        if (taskManager.getTaskById(taskId).getType().equals(TaskType.EPIC)) {
            subtasks = ((Epic) taskManager.getTaskById(taskId)).getSubtasks();
        } else {
            subtasks = new HashSet<>();
        }
        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(HTTP_PROTOCOL + HOSTNAME + ':' + TASK_SERVER_PORT + TASK + BY_ID + taskId))
                .version(CLIENT_VERSION)
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertNull(taskManager.getTaskById(taskId), "Задача не удалена");
        for (Integer id : subtasks) {
            assertNull(taskManager.getTaskById(id), "Задача не удалена");
        }
    }

    @Test
    void postTaskRequestsTest() {
        Task task = new Task("TestName", "TestDescription", TaskStatus.NEW);
        postTaskRequest(TASK, task);
        task.setId(taskManager.getIdCounter());
        assertEquals(task, taskManager.getTaskById(taskManager.getIdCounter()), "Задачи не совпадают");
        Integer randomInt = (int) (Math.random() * taskManager.getIdCounter() + 1);
        task.setId(randomInt);
        postTaskRequest(TASK, task);
        assertEquals(task, taskManager.getTaskById(randomInt), "Задачи не совпадают");

    }

    void testTaskCollectionWithServerResponse(Collection<Task> tasks, String partUrn) {

        HttpResponse<String> response = getResponseFromGetRequest(partUrn);

        List<Task> onServer = List.copyOf(tasks);
        List<Task> fromServer = List.copyOf (
                JsonTaskParser.parseJsonToTaskCollection(
                        gson,
                        response.body()));

        testLists(onServer, fromServer);
    }

    HttpResponse<String> getResponseFromGetRequest(String partUrn) {
        try {
            request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(HTTP_PROTOCOL + HOSTNAME + ':' + TASK_SERVER_PORT + partUrn))
                    .version(CLIENT_VERSION)
                    .build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            return httpClient.send(request, handler);
        } catch ( IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    void postTaskRequest(String partUrn, Task task) {
        try {
            request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                    .uri(URI.create(HTTP_PROTOCOL + HOSTNAME + ':' + TASK_SERVER_PORT + partUrn))
                    .version(CLIENT_VERSION)
                    .build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            httpClient.send(request, handler);
        } catch ( IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
