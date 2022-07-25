package managers;

import managers.tasks.FileBackedTasksManager;
import managers.tasks.HttpTaskManager;
import managers.tasks.InMemoryTasksManager;
import managers.tasks.TaskManager;

import java.nio.file.Path;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTasksManager();
    }
    public static TaskManager getFileBackedTasksManager(Path path) {
        return new FileBackedTasksManager(path);
    }
    public static TaskManager getHttpTaskManager(String url) {return new HttpTaskManager(url);}

}
