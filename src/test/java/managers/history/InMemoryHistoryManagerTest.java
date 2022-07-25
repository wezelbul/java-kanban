package managers.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    public void createHistory() {
        historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Помыть посуду",
                "Помыть посуду до возвращения мамы с работы",
                TaskStatus.NEW);
        Task epic1 = new Epic("Сдать ЕГЭ",
                "Сдать ЕГЭ на наивысший балл чтобы поступить в МГУ", TaskStatus.NEW);
        Task subtask1 = new Subtask("Найти репетитора",
                "Найти репетитора по физике",
                TaskStatus.NEW, 4);
        Task subtask2 = new Subtask("Прочитать Фейнмановские лекции по физике",
                "Все 9 томов",
                TaskStatus.NEW, 4);
        task1.setId(1);
        epic1.setId(2);
        subtask1.setId(3);
        subtask2.setId(4);
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        historyManager.add(task1);
    }

    @Test
    public void addTest() {
        List<Task> history = List.copyOf(historyManager.getHistory());

        Task task1 = new Task("Помыть посуду",
                "Помыть посуду до возвращения мамы с работы",
                TaskStatus.NEW);
        Task epic1 = new Epic("Сдать ЕГЭ",
                "Сдать ЕГЭ на наивысший балл чтобы поступить в МГУ", TaskStatus.NEW);
        Task subtask1 = new Subtask("Найти репетитора",
                "Найти репетитора по физике",
                TaskStatus.NEW, 2);
        Task subtask2 = new Subtask("Прочитать Фейнмановские лекции по физике",
                "Все 9 томов",
                TaskStatus.NEW, 2);
        task1.setId(1);
        epic1.setId(2);
        subtask1.setId(3);
        subtask2.setId(4);
        List<Task> testHistory = List.of(epic1, subtask1, subtask2, task1);
        testLists(history, testHistory);
    }

    private void testLists(List<Task> taskList, List<Task> testList) {
        assertNotNull(taskList, "Задачи не возвращаются");
        assertEquals(testList.size(), taskList.size(), "Неверное количество задач");
        for (int i = 0; i < taskList.size(); i++) {
            Task epic = taskList.get(i);
            Task epicTest = testList.get(i);
            assertEquals(epicTest, epic, "Задачи не совпадают");
        }
    }

    @Test
    public void removeFirstTest() {
        historyManager.remove(2);
        List<Task> history = List.copyOf(historyManager.getHistory());

        Task task1 = new Task("Помыть посуду",
                "Помыть посуду до возвращения мамы с работы",
                TaskStatus.NEW);
        Task subtask1 = new Subtask("Найти репетитора",
                "Найти репетитора по физике",
                TaskStatus.NEW, 2);
        Task subtask2 = new Subtask("Прочитать Фейнмановские лекции по физике",
                "Все 9 томов",
                TaskStatus.NEW, 2);
        task1.setId(1);
        subtask1.setId(3);
        subtask2.setId(4);
        List<Task> testHistory = List.of(subtask1, subtask2, task1);
        testLists(history, testHistory);
    }

    @Test
    public void removeMidTest() {
        historyManager.remove(4);
        List<Task> history = List.copyOf(historyManager.getHistory());

        Task task1 = new Task("Помыть посуду",
                "Помыть посуду до возвращения мамы с работы",
                TaskStatus.NEW);
        Task epic1 = new Epic("Сдать ЕГЭ",
                "Сдать ЕГЭ на наивысший балл чтобы поступить в МГУ", TaskStatus.NEW);
        Task subtask1 = new Subtask("Найти репетитора",
                "Найти репетитора по физике",
                TaskStatus.NEW, 2);
        task1.setId(1);
        epic1.setId(2);
        subtask1.setId(3);
        List<Task> testHistory = List.of(epic1, subtask1, task1);
        testLists(history, testHistory);
    }

    @Test
    public void removeLastTest() {
        historyManager.remove(1);
        List<Task> history = List.copyOf(historyManager.getHistory());

        Task epic1 = new Epic("Сдать ЕГЭ",
                "Сдать ЕГЭ на наивысший балл чтобы поступить в МГУ", TaskStatus.NEW);
        Task subtask1 = new Subtask("Найти репетитора",
                "Найти репетитора по физике",
                TaskStatus.NEW, 2);
        Task subtask2 = new Subtask("Прочитать Фейнмановские лекции по физике",
                "Все 9 томов",
                TaskStatus.NEW, 2);
        epic1.setId(2);
        subtask1.setId(3);
        subtask2.setId(4);
        List<Task> testHistory = List.of(epic1, subtask1, subtask2);
        testLists(history, testHistory);
    }

    @Test
    public void getHistoryTest() {
        addTest();
    }
}