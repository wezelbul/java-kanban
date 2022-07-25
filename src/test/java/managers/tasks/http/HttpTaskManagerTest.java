package managers.tasks.http;

import api.servers.HttpTaskServer;
import api.servers.KVServer;
import managers.tasks.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HttpTaskManagerTest extends FileBackedTasksManagerTest {

    KVServer kvServer;
    HttpTaskServer httpTaskServer;

    @Override
    @BeforeEach
    public void createManagerForTest() throws IOException {
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
    public void clear() {
        taskManager.deleteTask();
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Override
    protected void saveLoadTest() {
        List<Task> saveTaskArray = List.copyOf(taskManager.getAllTasks());
        List<Task>  saveHistoryArray = List.copyOf(taskManager.history());
        ((HttpTaskManager) taskManager).save();
        ((HttpTaskManager) taskManager).loadFromKvServer();
        List<Task>  loadTaskArray = List.copyOf(taskManager.getAllTasks());
        List<Task>  loadHistoryArray = List.copyOf(taskManager.history());
        testLists(saveTaskArray, loadTaskArray);
        testLists(saveHistoryArray, loadHistoryArray);
    }

}