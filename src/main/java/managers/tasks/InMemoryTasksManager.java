package managers.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import managers.history.*;
import tasks.*;

public class InMemoryTasksManager implements TaskManager {

    protected Map<Integer, Task> taskMap = new HashMap<>();
    protected Set<Task> sortedTasks = new TreeSet<>(Comparator.comparing(Task::getId));
    protected static Integer idCounter = 0;
    protected HistoryManager historyManager = new InMemoryHistoryManager();

    private boolean checkEmptyTaskMap() {
        return taskMap.size() == 0;
    }

    @Override
    public Collection<Task> getAllTasks() {
        if (checkEmptyTaskMap()) {
            return null;
        }
        return taskMap.values();
    }

    @Override
    public Collection<Task> getAllEpics() {
        if (checkEmptyTaskMap()) {
            return null;
        }
        Collection<Task> allEpics = new ArrayList<>();
        for (Task task : taskMap.values()) {
            if (task.getClass().equals(Epic.class)) {
                allEpics.add(task);
            }
        }
        return allEpics;
    }

    @Override
    public Collection<Task> getSubtasksById(Integer id) {
        if (checkEmptyTaskMap()
                || !taskMap.containsKey(id)) {
            return null;
        }
        Collection<Task> subtasks = new ArrayList<>();
        for (Task task : taskMap.values()) {
            if (task.getClass().equals(Subtask.class)) {
                Subtask subtask = (Subtask) task;
                if (subtask.getEpic().equals(id)) {
                    subtasks.add(task);
                }
            }
        }
        return subtasks;
    }

    @Override
    public Task getTaskById(Integer id) {
        if (checkEmptyTaskMap() || !taskMap.containsKey(id)) {
            return null;
        }
        Task task = taskMap.get(id);
        recordHistory(task);
        return task;
    }

    @Override
    public boolean addTask(Task task) {
        while (taskMap.containsKey(++idCounter));
        task.setId(idCounter);
        if (isSubtaskErrors(task)) {
            return false;
        }
        taskMap.put(idCounter, task);
        if (isSubtask(task)) {
            Subtask subtask = (Subtask) taskMap.get(task.getId());
            Task epic =  taskMap.get(subtask.getEpic());
            createEpicToSubtaskLink(epic, task);
            int epicId = epic.getId();
            updateEpicStatus(epicId);
            updateEpicStartTimeAndDuration(epicId);
            boolean isTimeCrossed = checkTaskTimeCrossing(task);
            if (isTimeCrossed) {
                task.resetStartTime();
                task.resetDuration();
            }
        }
        return true;
    }

    @Override
    public boolean updateTaskById(Task task, Integer id) {
        if (isSubtaskErrors(task) || id <= 0) {
            return false;
        }
        Task oldTask = taskMap.get(id);
        if (oldTask != null) {
            breakLinks(oldTask);
        }
        if (isSubtask(task)) {
            Subtask subtask = (Subtask) task;
            Task epic = taskMap.get(subtask.getEpic());
            if (isEpic(epic)) {
                Integer epicId = subtask.getEpic();
                if (epicId.equals(task.getId())) {
                    return false;
                }
                taskMap.put(id, task);
                task.setId(id);
                createEpicToSubtaskLink(epic, task);
                updateEpicStatus(epicId);
                updateEpicStartTimeAndDuration(epicId);
                boolean isTimeCrossed = checkTaskTimeCrossing(task);
                if (isTimeCrossed) {
                    task.resetStartTime();
                    task.resetDuration();
                }
            }
        } else if (isEpic(task)) {
            Collection<Integer> subtasksList = ((Epic) task).getSubtasks();
            if (!subtasksList.isEmpty()) {
                for (Integer subtaskId : subtasksList) {
                    if (!taskMap.containsKey(subtaskId)
                            || subtaskId.equals(task.getId())) {
                        continue;
                    }


                    Task subtask = taskMap.get(subtaskId);
                    createEpicToSubtaskLink(task, subtask);
                }
            }
            taskMap.put(id, task);
            task.setId(id);
            updateEpicStatus(id);
            updateEpicStartTimeAndDuration(id);
            boolean isTimeCrossed = checkTaskTimeCrossing(task);
            if (isTimeCrossed) {
                task.resetStartTime();
                task.resetDuration();
            }
        } else {
            taskMap.put(id, task);
        }
        return true;
    }

    private void createEpicToSubtaskLink(Task epic, Task subtask) {
        ((Epic) epic).getSubtasks().add(subtask.getId());
        ((Subtask) subtask).setEpic(epic.getId());
    }

    private void resetCounter() {
        idCounter = 0;
    }

    @Override
    public void deleteTask() {
        taskMap = new HashMap<>();
        resetCounter();
        clearHistory();
    }

    @Override
    public void deleteTask(Integer id) {
        Task task = taskMap.get(id);
        if (task == null) {
            return;
        }
        if (isEpic(task)) {
            Collection<Integer> subtasks = ((Epic) task).getSubtasks();
            for (Integer subtaskId : subtasks) {
                taskMap.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
        } else if (isSubtask(task)) {
            breakLinks(task);
            Subtask subtask = (Subtask) task;
            int epicId = subtask.getEpic();
            updateEpicStatus(epicId);
            updateEpicStartTimeAndDuration(epicId);
        }
        taskMap.remove(id);
        historyManager.remove(id);
        if (checkEmptyTaskMap()) {
            deleteTask();
        }
    }

    private boolean isEpic(Task task) {
        return task.getClass().equals(Epic.class);
    }

    private boolean isSubtask(Task task) {
        return task.getClass().equals(Subtask.class);
    }

    private boolean isSubtaskItself(Task task) {
        return ((Subtask) task).getEpic().equals(task.getId());
    }

    private boolean isSubtaskToSubtask(Task task) {
        Subtask subtask = (Subtask) task;
        Task checkTask = taskMap.get(subtask.getEpic());
        return (checkTask.getClass().equals(Subtask.class));
    }

    private boolean isSubtaskErrors(Task task) {
        return isSubtask(task)
                && (isSubtaskItself(task)
                || isSubtaskToSubtask(task));
    }

    private void updateEpicStatus(Integer id) {
        Task task = taskMap.get(id);
        Epic epic = (Epic) task;
        int doneCount = 0;
        int progressCount = 0;
        Set<Integer> subtasksSet =  new HashSet<>(epic.getSubtasks());
        if (!epic.getSubtasks().isEmpty()) {

            for (Integer subtaskID : epic.getSubtasks()) {
                Task subtask = taskMap.get(subtaskID);
                if (subtask == null) {
                    subtasksSet.remove(subtaskID);
                    continue;
                }
                if (subtask.getStatus().equals(TaskStatus.DONE)) {
                    doneCount++;
                } else if (subtask.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                    progressCount++;
                }
            }
            epic.setSubtasksSet(subtasksSet);
        }
        if (doneCount == 0 && progressCount == 0) {
            epic.setStatus(TaskStatus.NEW);
        } else if (doneCount == subtasksSet.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void breakLinks(Task task) {
        if (isEpic(task)) {
            Collection<Integer> subtaskList = ((Epic) task).getSubtasks();
            if (!subtaskList.isEmpty()) {
                for (Integer subtaskId : subtaskList) {
                    Task subtask = taskMap.get(subtaskId);
                    ((Subtask) subtask).setEpic(null);
                    taskMap.put(subtaskId, subtask);
                }
            }
        } else if (isSubtask(task)) {
            Integer epicId = ((Subtask) task).getEpic();
            Task epic = taskMap.get(epicId);
            ((Epic) epic).getSubtasks().remove(task.getId());
            taskMap.put(epicId, epic);
        }
    }

    @Override
    public Collection<Task> history() {
        return historyManager.getHistory();
    }

    private void recordHistory(Task task) {
        historyManager.add(task);
    }

    private void clearHistory() {
        if (historyManager != null) {
            if (historyManager.getHistory() != null) {
                Collection<Task> history = historyManager.getHistory();
                for (Task task : history) {
                    Integer id = task.getId();
                    historyManager.remove(id);
                }
            }
        }
    }

    public void updateEpicStartTimeAndDuration(Integer id) {
        Task epic = taskMap.get(id);
        List<Task> subtasks = List.copyOf(getSubtasksById(id));
        if (subtasks != null && !subtasks.isEmpty()) {
            LocalDateTime earlyStartTime = subtasks.get(0).getStartTime();
            LocalDateTime latestEndTime = subtasks.get(0).getStartTime();
            if (earlyStartTime == null) {
                earlyStartTime = LocalDateTime.MAX;
                latestEndTime = LocalDateTime.MIN;
            }
            Duration durationSum = Duration.ZERO;
            for (Task sub : subtasks) {

                if (sub.getDuration() != null) {
                    durationSum = durationSum.plus(sub.getDuration());
                }

                LocalDateTime subStartTime = sub.getStartTime();
                if (subStartTime != null && subStartTime.isBefore(earlyStartTime)) {
                    earlyStartTime = subStartTime;
                }

                LocalDateTime subEndTime = sub.getEndTime();
                if (subStartTime != null && subEndTime.isAfter(latestEndTime)) {
                    latestEndTime = subEndTime;
                }
            }
            if (earlyStartTime.equals(LocalDateTime.MAX)) {
                epic.resetStartTime();
            } else {
                epic.setStartTime(earlyStartTime);
            }
            if (durationSum.equals(Duration.ZERO)) {
                epic.resetDuration();
            } else {
                epic.setDuration(durationSum);
            }
            if (!latestEndTime.equals(LocalDateTime.MIN)) {
                ((Epic) epic).setEndTime(latestEndTime);
            }
        }
    }

    @Override
    public Collection<Task> getPrioritizedTasks() {

        Collection<Task> allTasks = getAllTasks();
        Collection<Task> result = new ArrayList<>();;
        Collection<Task> nullStartTime = new ArrayList<>();
        Collection<Task> haveStartTime = new ArrayList<>();

        if (allTasks != null) {
            if (sortedTasks != null && sortedTasks.size() == allTasks.size()) {
                result = new ArrayList<>(sortedTasks);
            } else {
                for (Task task : allTasks) {
                    if (task.getStartTime() == null) {
                        nullStartTime.add(task);
                    } else {
                        haveStartTime.add(task);
                    }
                }
                result = haveStartTime
                        .stream()
                        .sorted(Comparator.comparing(Task::getStartTime))
                        .collect(Collectors.toList());
                sortedTasks = new TreeSet<>(Comparator.comparing(Task::getId));
                sortedTasks.addAll(result);
            }
            result.addAll(nullStartTime);
        }

        return result;
    }

    private boolean checkTaskTimeCrossing(Task task) {
        LocalDateTime taskStartTime = task.getStartTime();
        LocalDateTime taskEndTime = task.getEndTime();
        for (Task eachTask : sortedTasks) {
            LocalDateTime anotherTaskStartTime = eachTask.getStartTime();
            LocalDateTime anotherTaskEndTime = eachTask.getEndTime();
            if (task.getType().equals(TaskType.SUBTASK)) {
                Integer epicId = ((Subtask) task).getEpic();
                if (eachTask.getId().equals(epicId)) {
                    return false;
                }
            } else if (task.getType().equals(TaskType.EPIC)) {
                Collection<Integer> subtasksList = ((Epic) task).getSubtasks();
                if (subtasksList.contains(eachTask.getId())) {
                    return false;
                }
            } else if (!task.equals(eachTask)
                && !taskStartTime.isEqual(anotherTaskStartTime)
                && !taskEndTime.isEqual(anotherTaskEndTime)
                && taskStartTime.isAfter(anotherTaskEndTime)
                && taskEndTime.isBefore(anotherTaskStartTime)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer getIdCounter() {
        return idCounter;
    }
}

