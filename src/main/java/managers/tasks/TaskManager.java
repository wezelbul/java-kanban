package managers.tasks;

import java.util.Collection;
import tasks.*;

public interface TaskManager {

    Collection<Task> getAllTasks();

    Collection<Task> getAllEpics();

    Collection<Task> getSubtasksById(Integer id);

    Task getTaskById(Integer id);

    boolean addTask(Task task);

    boolean updateTaskById(Task task, Integer id);

    void deleteTask();

    void deleteTask(Integer id);

    Collection<Task> history();

    Collection<Task> getPrioritizedTasks();

    Integer getIdCounter();
}
