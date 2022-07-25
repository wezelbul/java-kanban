package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

    private String name;
    private String description;
    private Integer id;
    private TaskStatus status;
    private TaskType type;
    private Duration duration;
    private LocalDateTime startTime;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
        setType();
        this.duration = Duration.ZERO;
    }

    @Override
    public String toString() {
        String breakLine = "\n";
        return breakLine + "Идентификатор: " + this.id + breakLine
                + "Задача: " + this.name + breakLine
                + "Описание: " + this.description + breakLine
                + "Статус: " + this.status.getName() + breakLine
                + "Начало: " + this.startTime + breakLine
                + "Длительность: " + this.duration.toMinutes() + breakLine;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task object = (Task) obj;
        return this.id.equals(object.id)
                && this.name.equals(object.name)
                && this.description.equals(object.description)
                && this.status.equals(object.status)
                && this.type.equals(object.type);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    private void setType() {
        for (TaskType type : TaskType.values()) {
            if (this.getClass().equals(type.getType())) {
                this.type = type;
            }
        }
    }

    public TaskType getType() {
        return type;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setDuration(long minutes) {
        this.duration = Duration.ofMinutes(minutes);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = LocalDateTime.parse(startTime, FORMATTER);
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    public void resetDuration() {
        duration = Duration.ZERO;
    }

    public void resetStartTime() {
        startTime = null;
    }
}
