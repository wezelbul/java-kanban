package tasks;

public enum TaskType {

    TASK("Обычная", Task.class),
    SUBTASK("Подзадача", Subtask.class),
    EPIC("Составная", Epic.class);

    private final String name;
    private final Class<? extends Task> type;

    TaskType (String name, Class<? extends Task> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Task> getType() {
        return type;
    }

}
