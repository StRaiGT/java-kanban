package model;

public class Subtask extends Task {
    // Подзадача не может существовать без эпика
    private final int epicId;
    private final TaskType taskType = TaskType.SUBTASK;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(int id, Subtask subtask) {
        super(id, subtask);
        this.epicId = subtask.getEpicId();
    }
    public Subtask(Subtask subtask, State status) {
        super(subtask, status);
        this.epicId = subtask.getEpicId();
    }

    public Subtask(int id, String name, State status, String description, int epicId) {
        super(id, name, status, description);
        this.epicId =  epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public String toString() {
        return String.format("(Id - '%d', Имя - '%s', Описание - '%s', статус - '%s', EpicId - '%d')",
                getId(), getName(), getDescription(), getStatus(), getEpicId());
    }
}
