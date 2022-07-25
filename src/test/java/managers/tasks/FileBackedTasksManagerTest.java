package managers.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private final Path TEST_FILE = Paths.get("CSV\\test_tasks.csv");

    @Override
    @BeforeEach
    public void createManagerForTest() throws IOException {
        taskManager = new FileBackedTasksManager();
        createTestTasks();
        createTestHistory();
    }

    @Override
    @AfterEach
    public void clear() {
        taskManager.deleteTask();
        taskManager.save();
    }

    @Override
    @Test
    protected void addTaskTest() {
        super.addTaskTest();
        saveLoadTest();
    }

    @Override
    @Test
    protected void getTaskByIdTest() {
        super.getTaskByIdTest();
        saveLoadTest();
    }

    @Override
    @Test
    protected void updateTaskByIdTest() {
        super.updateTaskByIdTest();
        saveLoadTest();
    }

    @Override
    @Test
    protected void deleteAllTasksTest() {
        super.deleteAllTasksTest();
        saveLoadTest();
    }

    @Override
    @Test
    protected void deleteTaskByIdTest() {
        super.deleteTaskByIdTest();
        saveLoadTest();
    }

    protected void saveLoadTest() {
        FileBackedTasksManager testManager = FileBackedTasksManager.loadFromFile(taskManager.getTasksFile());
        testManager.setTasksFile(TEST_FILE);
        testManager.save();
        Path file = taskManager.getTasksFile();
        try (BufferedReader taskManagerReader = new BufferedReader(new FileReader(taskManager.getRealPath(file)))) {
            BufferedReader testManagerReader = new BufferedReader(new FileReader(testManager.getRealPath(TEST_FILE)));
            while (taskManagerReader.ready() && testManagerReader.ready()) {
                String line = taskManagerReader.readLine();
                String testLine = testManagerReader.readLine();
                assertEquals(line, testLine, "Задачи не совпадают");
                if ((!taskManagerReader.ready() && testManagerReader.ready()) ||
                        (taskManagerReader.ready() && !testManagerReader.ready())) {
                    assertTrue(taskManagerReader.ready(), "Оригинальный файл длиннее тестового");
                    assertTrue(testManagerReader.ready(), "Тестовый файл длиннее оригинального");
                }
            }
            testManager.deleteTask();
            testManager.save();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}