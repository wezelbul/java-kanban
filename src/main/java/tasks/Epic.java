package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Epic extends tasks.Task {

    private Set<Integer> subtasks = new HashSet<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, TaskStatus status, Set<Integer> subtask) {
        super(name, description, status);
        this.subtasks = subtask;
    }

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        this.subtasks = new HashSet<>();
    }

    @Override
    public String toString() {
        String breakLine = "\n";
        String result = breakLine + "Идентификатор: " + getId() + breakLine
                + "Задача: " + getName() + breakLine
                + "Описание: " + getDescription() + breakLine
                + "Статус: " + getStatus().getName() + breakLine
                + "Начало: " + getStartTime() + breakLine
                + "Длительность: " + getDuration().toMinutes() + breakLine;
        if (!this.subtasks.isEmpty()) {
            String idList = "";
            for (int id : this.subtasks) {
                idList += id + ", ";
            }
            idList = idList.substring(0, idList.length() - 2);
            result += "Включает в себя подзадачи со следующими идентификаторами: "
                    + idList + breakLine;
        }
        return result;
    }

    public Set<Integer> getSubtasks() {
        return subtasks;
    }

    public void setSubtask(Integer id) {
        this.subtasks.add(id);
    }

    public void setSubtasksSet(Set<Integer> subtasks) {
        this.subtasks = subtasks;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

}