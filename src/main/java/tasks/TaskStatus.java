package tasks;

public enum TaskStatus {
    NEW(TaskStatus.WHITE_BOLD,"Новая"),
    IN_PROGRESS(TaskStatus.YELLOW_BOLD, "Выполняется"),
    DONE(TaskStatus.GREEN_BOLD, "Выполнена");

    private final String name;
    private static final String WHITE_BOLD = "\033[1;37m";
    private static final String YELLOW_BOLD = "\033[1;33m";
    private static final String GREEN_BOLD = "\033[1;32m";
    private static final String TEXT_RESET = "\033[0m";

    TaskStatus(String color, String name) {
        this.name = color + name + TEXT_RESET;
    }

    public String getName() {
        return name;
    }
}

