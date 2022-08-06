package model;

public class Task {
    private String name;
    private String description;
    private final State status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = State.NEW;
    }

    public Task(Task task, State status) {
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public State getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("(Имя - '%s', Описание - '%s', статус - '%s')", name, description, status);
    }
}
