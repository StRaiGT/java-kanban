package model;

public class Task {
    private int id;
    private final String name;
    private final String description;
    private final State status;
    private final TaskType taskType = TaskType.TASK;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = State.NEW;
    }

    public Task(int id, Task task) {
        this.id = id;
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = task.status;
    }

    public Task(Task task, State status) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = status;
    }

    public Task(int id, String name, State status, String description) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public int getId() {
        return id;
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

    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public String toString() {
        return String.format("(Id - '%d', Имя - '%s', Описание - '%s', статус - '%s')",
                getId(), getName(), getDescription(), getStatus());
    }
}
