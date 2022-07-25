package tasks;

public class Subtask extends Task {

    private Integer epic = null;

    public Subtask(String name, String description, TaskStatus status, Integer epic) {
        super(name, description, status);
        this.epic = epic;
    }

    public Integer getEpic() {
        return epic;
    }

    public void setEpic(Integer epicId) {
        this.epic = epicId;
    }

    @Override
    public String toString() {
        String breakLine = "\n";
        String result =  breakLine + "Идентификатор: " + getId() + breakLine
                + "Задача: " + getName() + breakLine
                + "Описание: " + getDescription() + breakLine
                + "Статус: " + getStatus().getName() + breakLine
                + "Начало: " + getStartTime() + breakLine
                + "Длительность: " + getDuration().toMinutes() + breakLine;
        if (this.epic != null) {
            result += "Является подзадачей для задачи с идентификатором: " + this.epic + breakLine;
        }
        return result;
    }
}
