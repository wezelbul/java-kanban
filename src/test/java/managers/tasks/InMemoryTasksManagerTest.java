package managers.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTasksManagerTest extends TaskManagerTest<InMemoryTasksManager> {

    @Override
    @BeforeEach
    public void createManagerForTest() throws IOException {
        taskManager = new InMemoryTasksManager();
        taskManager.deleteTask();
        createTestTasks();
    }

    @Override
    @AfterEach
    public void clear() {
        taskManager.deleteTask();
    }

    @Test
    void breakLinksTest() {
        Task epicTest = taskManager.getTaskById(4);
        Collection<Task> subtasksLinks = taskManager.getSubtasksById(4);
        taskManager.breakLinks(epicTest);
        for (Task sub : subtasksLinks) {
            assertNull(((Subtask) sub).getEpic(), "Ссылка на эпик не удалена");
        }

        Integer epicId = 5;
        Set<Integer> subtasksList = new HashSet<>(List.of(9, 10));
        Collection<Integer> subForEach = new ArrayList<>(subtasksList);
        for (Integer id : subForEach) {
            Task subtaskTest = taskManager.getTaskById(id);
            subtasksList.remove(subtaskTest.getId());
            taskManager.breakLinks(subtaskTest);
            assertEquals(subtasksList, ((Epic) taskManager.getTaskById(epicId)).getSubtasks(),
                    "Ссылка на сабтаск не удалена");
        }
    }

    @Test
    void updateEpicStatusTest() {
        Task epic = taskManager.getTaskById(4);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус не соответствует NEW"); // all NEW
        Task subtask2 = new Subtask("Прочитать Фейнмановские лекции по физике",
                "Все 9 томов",
                TaskStatus.DONE, 4);
        taskManager.updateTaskById(subtask2, 8);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Статус не соответствует IN_PROGRESS"); // NEW and DONE
        Task subtask1 = new Subtask("Найти репетитора",
                "Найти репетитора по физике",
                TaskStatus.DONE, 4);
        taskManager.updateTaskById(subtask1, 7);
        assertEquals(TaskStatus.DONE, epic.getStatus(),"Статус не соответствует DONE"); // all DONE
        subtask1 = new Subtask("Найти репетитора",
                "Найти репетитора по физике",
                TaskStatus.IN_PROGRESS, 4);
        taskManager.updateTaskById(subtask1, 7);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Статус не соответствует IN_PROGRESS"); // DONE and IN_PROGRESS
        subtask2 = new Subtask("Прочитать Фейнмановские лекции по физике",
                "Все 9 томов",
                TaskStatus.IN_PROGRESS, 4);
        taskManager.updateTaskById(subtask2, 8);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Статус не соответствует IN_PROGRESS"); // all IN_PROGRESS
        subtask2  = new Subtask("Прочитать Фейнмановские лекции по физике",
                "Все 9 томов",
                TaskStatus.NEW, 4);
        taskManager.updateTaskById(subtask2, 8);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Статус не соответствует IN_PROGRESS"); // IN_PROGRESS and NEW
        taskManager.deleteTask(7);
        taskManager.deleteTask(8);
        assertEquals(TaskStatus.NEW, epic.getStatus(),
                "Статус не соответствует NEW"); // empty list
    }

    @Test
    public void updateEpicStartTimeAndDurationTest() {
        taskManager.updateEpicStartTimeAndDuration(4);
        Task epic = taskManager.getTaskById(4);

        assertNull(epic.getStartTime());
        assertEquals(Duration.ZERO, epic.getDuration());

        taskManager.getTaskById(7)
                .setStartTime("08:00 19.01.2021");
        taskManager.getTaskById(7)
                .setDuration(30);
        taskManager.getTaskById(8)
                .setStartTime("07:00 19.01.2021");
        taskManager.getTaskById(8)
                .setDuration(65);
        taskManager.updateEpicStartTimeAndDuration(4);

        Duration testDuration = Duration.ofMinutes(95);
        LocalDateTime testStartTime = LocalDateTime.parse("07:00 19.01.2021", Task.FORMATTER);
        LocalDateTime testEndTime = LocalDateTime.parse("08:30 19.01.2021", Task.FORMATTER);

        assertEquals(testDuration, epic.getDuration());
        assertEquals(testStartTime, epic.getStartTime());
        assertEquals(testEndTime, epic.getEndTime());
    }

    @Test
    public void getPrioritizedTasksTest() {

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

        List<Task> taskList = List.copyOf(taskManager.getPrioritizedTasks());
        List<Task> testList = List.of(
                taskManager.getTaskById(6),
                taskManager.getTaskById(12),
                taskManager.getTaskById(5),
                taskManager.getTaskById(10),
                taskManager.getTaskById(11),
                taskManager.getTaskById(1),
                taskManager.getTaskById(2),
                taskManager.getTaskById(3),
                taskManager.getTaskById(4),
                taskManager.getTaskById(7),
                taskManager.getTaskById(8),
                taskManager.getTaskById(9)
        );
        testLists(taskList, testList);
    }

}