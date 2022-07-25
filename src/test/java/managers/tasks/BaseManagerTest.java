package managers.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class BaseManagerTest <T extends TaskManager> {

    protected T taskManager;

    @BeforeEach
    protected abstract void createManagerForTest() throws IOException;
    @AfterEach
    protected abstract void clear();

    protected void createTestTasks() {

        Task task1 = new Task("Помыть посуду",
                "Помыть посуду до возвращения мамы с работы",
                TaskStatus.NEW);
        Task task2 = new Task("Купить хлеба",
                "Купить хлеба по пути из школы",
                TaskStatus.DONE);
        Task task3 = new Task("Сделать уроки",
                "Алгебра геометрия история",
                TaskStatus.IN_PROGRESS);

        Task epic1 = new Epic("Сдать ЕГЭ",
                "Сдать ЕГЭ на наивысший балл чтобы поступить в МГУ", TaskStatus.NEW);
        Task epic2 = new Epic("Накопить денег на ноутбук",
                "Накопить 20.000 рублей", TaskStatus.NEW);
        Task epic3 = new Epic("Снять любительское кино",
                "Попробовать свои силы в режиссуре", TaskStatus.NEW);

        Task subtask1 = new Subtask("Найти репетитора",
                "Найти репетитора по физике",
                TaskStatus.NEW, 4);
        Task subtask2 = new Subtask("Прочитать Фейнмановские лекции по физике",
                "Все 9 томов",
                TaskStatus.NEW, 4);
        Task subtask3 = new Subtask("Найти подработку",
                "Найти летнюю подработку",
                TaskStatus.DONE, 5);
        Task subtask4 = new Subtask("Экономить на школьных обедах",
                "Откладывать каждый день по 50 рублей",
                TaskStatus.IN_PROGRESS, 5);
        Task subtask5 = new Subtask("Найти камеру",
                "Найти у кого из знакомых есть видеокамера",
                TaskStatus.DONE, 6);
        Task subtask6 = new Subtask("Провести съёмки",
                "Найти день чтобы всем было удобно и снять короткий метр",
                TaskStatus.DONE, 6);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addTask(epic1);
        taskManager.addTask(epic2);
        taskManager.addTask(epic3);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        taskManager.addTask(subtask3);
        taskManager.addTask(subtask4);
        taskManager.addTask(subtask5);
        taskManager.addTask(subtask6);
    }

    protected void createTestHistory() {
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(3);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
    }

    protected void testLists(List<Task> taskList, List<Task> testList) {
        assertNotNull(testList, "Задачи не возвращаются");
        assertEquals(taskList.size(), testList.size(), "Неверное количество задач");
        for (int i = 0; i < testList.size(); i++) {
            Task epic = testList.get(i);
            Task epicTest = taskList.get(i);
            assertEquals(epicTest, epic, "Задачи не совпадают");
        }
    }


}
