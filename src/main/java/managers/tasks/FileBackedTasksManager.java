package managers.tasks;

import exceptions.ManagerSaveException;
import managers.history.HistoryManager;
import tasks.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FileBackedTasksManager extends InMemoryTasksManager {

    private Path tasksFile;
    private static final Path DEFAULT_FILE = Paths.get("CSV\\tasks.csv");
    private static final String HEAD = "id,type,name,status,description,start_time,duration_in_minutes,epic/subtasks";
    private static final String SEPARATOR = ",";
    private static final String NEW_LINE = "\n";

    public Path getTasksFile() {
        return tasksFile;
    }

    public void setTasksFile(Path tasksFile) {
        this.tasksFile = tasksFile;
    }

    public FileBackedTasksManager(Path tasksFile) {
        this.tasksFile = tasksFile;
    }

    public FileBackedTasksManager () {
        this.tasksFile = DEFAULT_FILE;
    }

    @Override
    public boolean addTask(Task task) {
        if(super.addTask(task)) {
            save();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public boolean updateTaskById(Task task, Integer id) {
        if(super.updateTaskById(task, id)) {
            save();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deleteTask(Integer id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteTask() {
        super.deleteTask();
        save();
    }

    private String taskToString(Task task) {
        String result = task.getId() + SEPARATOR + task.getType() + SEPARATOR
                + task.getName() + SEPARATOR + task.getStatus() + SEPARATOR
                + task.getDescription() + SEPARATOR + task.getStartTime() + SEPARATOR
                + task.getDuration().toMinutes();
        if (task.getType().equals(TaskType.SUBTASK)) {
            Subtask subtask = (Subtask) task;
            result += SEPARATOR + subtask.getEpic();
        } else if (task.getType().equals(TaskType.EPIC)) {
            Epic epic = (Epic) task;
            for (Integer id : epic.getSubtasks()) {
                result += SEPARATOR + id;
            }
        }
        return result;
    }

    private static Task taskFromString(String value) {
        int epicOrSubtaskArrayIndex = 7;
        Task task;

        String[] values = value.split(SEPARATOR);
        try {
            Integer id = Integer.parseInt(values[0]);
            TaskType type = TaskType.valueOf(values[1]);
            String name = values[2];
            TaskStatus status = TaskStatus.valueOf(values[3]);
            String description = values[4];
            LocalDateTime startTime;
            if (values[5].equals("null")) {
                startTime = null;
            } else {
                startTime = LocalDateTime.parse(values[5]);
            }
            Duration duration;
            if (values[6].equals("null")) {
                duration = null;
            } else {
                duration = Duration.ofMinutes(Long.parseLong(values[6]));
            }
            if (type.equals(TaskType.SUBTASK)) {
                Integer epic = Integer.parseInt(values[7]);
                task = new Subtask(name, description, status, epic);
            } else if (type.equals(TaskType.EPIC)) {
                Set<Integer> subtasks = new HashSet<>();
                for (int i = epicOrSubtaskArrayIndex; i < values.length; i++ ) {
                    Integer subtask = Integer.parseInt(values[i]);
                    subtasks.add(subtask);
                }
                task = new Epic(name, description, status, subtasks);
            } else {
                task = new Task(name, description, status);
            }
            task.setId(id);
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.");
        }
        return task;
    }

    protected String historyToString (HistoryManager manager) {
        String result = "";
        if (manager != null) {
            Collection<Task> historyList = manager.getHistory();
            if (historyList != null) {
                for (Task task : historyList) {
                    result += task.getId() + SEPARATOR;
                }
                result = result.substring(0, result.length() - 1); //отсечь последнюю запятую
            }
        }
        return result;
    }

    protected static Collection<Integer> historyIdFromString(String value) {
        String[] tasksId = value.split(SEPARATOR);
        Collection<Integer> result = new ArrayList<>();
        for (String stringId : tasksId) {
            Integer id = Integer.parseInt(stringId);
            result.add(id);
            }
        return result;
    }

    public void save() {
        String filePath = getRealPath(tasksFile);
        Collection<Task> taskList = super.getAllTasks();

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            if (!Files.exists(tasksFile)) {
                Files.createFile(tasksFile);
            }
            fileWriter.write(HEAD + NEW_LINE);
            if (taskList != null) {
                for (Task task : taskList) {
                    fileWriter.write(taskToString(task) + NEW_LINE);
                }
                fileWriter.write(NEW_LINE);
                String history = historyToString(historyManager);
                if (history != null) {
                    fileWriter.write(history);
                }
            }
        } catch (IOException ioException) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }
    }

    public static FileBackedTasksManager loadFromFile(Path file) {
        idCounter = 0;
        if (!Files.exists(file)) {
            return new FileBackedTasksManager();
        }
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        String filePath = fileManager.getRealPath(file);

        try (BufferedReader reader = new BufferedReader (new FileReader(filePath))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (!line.isEmpty()) {
                    if (line.equals(HEAD)) {
                        continue;
                    }
                    Task task = taskFromString(line);
                    fileManager.taskMap.put(task.getId(), task);

                    Integer intermediate = task.getId();
                    if (idCounter < intermediate) {
                        idCounter = intermediate;
                    }

                } else {
                    line = reader.readLine();
                    if (line == null) {
                        fileManager.historyManager = null;
                    } else {
                        ArrayList<Integer> historyIdTasks = (ArrayList<Integer>) historyIdFromString(line);
                        for (int id : historyIdTasks) {
                            Task task = fileManager.taskMap.get(id);
                            fileManager.historyManager.add(task);
                        }
                    }
                }
            }

        } catch (IOException ioException) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.");
        }
        return fileManager;
    }

    public String getRealPath(Path path) {
        String filePath = null;
        try {
            filePath = path.toRealPath().toString();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return filePath;
    }

}
