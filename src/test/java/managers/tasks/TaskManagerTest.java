package managers.tasks;

import org.junit.jupiter.api.Test;
import tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest <T extends TaskManager> extends BaseManagerTest<T> {

    @Test
    protected void getAllTasksTest() {
        List<Task> allTasks = List.copyOf(taskManager.getAllTasks());

        Task task1 = new Task("Помыть посуду",
                "Помыть посуду до возвращения мамы с работы",
                TaskStatus.NEW);
        Task task2 = new Task("Купить хлеба",
                "Купить хлеба по пути из школы",
                TaskStatus.DONE);
        Task task3 = new Task("Сделать уроки",
                "Алгебра геометрия история",
                TaskStatus.IN_PROGRESS);
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);

        Task epic1 = new Epic("Сдать ЕГЭ",
                "Сдать ЕГЭ на наивысший балл чтобы поступить в МГУ", TaskStatus.NEW);
        Task epic2 = new Epic("Накопить денег на ноутбук",
                "Накопить 20.000 рублей", TaskStatus.IN_PROGRESS);
        Task epic3 = new Epic("Снять любительское кино",
                "Попробовать свои силы в режиссуре", TaskStatus.DONE);
        epic1.setId(4);
        epic2.setId(5);
        epic3.setId(6);

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
        subtask1.setId(7);
        subtask2.setId(8);
        subtask3.setId(9);
        subtask4.setId(10);
        subtask5.setId(11);
        subtask6.setId(12);

        List<Task> taskTestList = List.of(
                task1, task2, task3,
                epic1, epic2, epic3,
                subtask1, subtask2,
                subtask3, subtask4,
                subtask5, subtask6);

        testLists(allTasks, taskTestList);
    }

    @Test
    protected void getAllEpicsTest() {
        List<Task> allEpics = List.copyOf(taskManager.getAllEpics());

        Task epic1 = new Epic("Сдать ЕГЭ",
                "Сдать ЕГЭ на наивысший балл чтобы поступить в МГУ", TaskStatus.NEW);
        Task epic2 = new Epic("Накопить денег на ноутбук",
                "Накопить 20.000 рублей", TaskStatus.IN_PROGRESS);
        Task epic3 = new Epic("Снять любительское кино",
                "Попробовать свои силы в режиссуре", TaskStatus.DONE);
        epic1.setId(4);
        epic2.setId(5);
        epic3.setId(6);

        List<Task> epicTestList = List.of(epic1, epic2, epic3);
        testLists(allEpics, epicTestList);
    }

    @Test
    protected void getSubtasksByIdTest() {
        List<Task> subtaskList = List.copyOf(taskManager.getSubtasksById(4));

        Task subtask1 = new Subtask("Найти репетитора",
                "Найти репетитора по физике",
                TaskStatus.NEW, 4);
        Task subtask2 = new Subtask("Прочитать Фейнмановские лекции по физике",
                "Все 9 томов",
                TaskStatus.NEW, 4);
        subtask1.setId(7);
        subtask2.setId(8);

        List<Task> subtaskTestList = List.of(subtask1, subtask2);
        testLists(subtaskList, subtaskTestList);
    }

    @Test
    protected void getTaskByIdTest() {
        Task task = taskManager.getTaskById(2);

        Task task2 = new Task("Купить хлеба",
                "Купить хлеба по пути из школы",
                TaskStatus.DONE);
        task2.setId(2);

        assertEquals(task2, task, "Задачи не совпадают");
    }

    @Test
    protected void addTaskTest() {
        Task subtask7 = new Subtask("Выложить на ютуб",
                "Загрузить видео в сеть для обратной связи",
                TaskStatus.DONE, 12); //попытка сделать эпиком подзадачу
        taskManager.addTask(subtask7);
        assertEquals(12, taskManager.getAllTasks().size(), "Ошибка: задача добавлена вопреки логике");
        subtask7 = new Subtask("Выложить на ютуб",
                "Загрузить видео в сеть для обратной связи",
                TaskStatus.DONE, 6);
        taskManager.addTask(subtask7);
        assertEquals(13, taskManager.getAllTasks().size(), "Ошибка: пропущено добавление задачи");
    }

    @Test
    protected void updateTaskByIdTest() {
        Task subtask7 = new Subtask("Выложить на ютуб",
                "Загрузить видео в сеть для обратной связи",
                TaskStatus.DONE, 6);
        int taskId = 12;
        taskManager.updateTaskById(subtask7, taskId);
        assertEquals(subtask7, taskManager.getTaskById(taskId), "Задачи не совпадают");
    }

    @Test
    protected void deleteAllTasksTest() {
        taskManager.deleteTask();
        assertNull(taskManager.getAllTasks(), "Список задач не пуст");
        assertNull(taskManager.history(), "История вызовов не пуста");
        Task task2 = new Task("Купить хлеба",
                "Купить хлеба по пути из школы",
                TaskStatus.DONE);
        task2.setId(1);
        taskManager.addTask(task2);
        assertEquals(task2, taskManager.getTaskById(1), "Задачи не совпадают");
    }

    @Test
    protected void deleteTaskByIdTest() {
        taskManager.deleteTask(4);
        assertNull(taskManager.getTaskById(4), "Задача не удалена");
        assertNull(taskManager.getTaskById(7), "Задача не удалена");
        assertNull(taskManager.getTaskById(8), "Задача не удалена");
    }

    @Test
    protected void historyTest() {
        taskManager.deleteTask();
        assertNull(taskManager.history());
        createTestTasks();
        createTestHistory();
        assertEquals(3, taskManager.history().size(), "Неверное количество задач");
    }

}
