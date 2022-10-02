package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

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

    public Subtask(Subtask subtask, Duration duration, LocalDateTime startTime) {
        super(subtask, duration, startTime);
        this.epicId = subtask.getEpicId();
    }

    public Subtask(int id, String name, State status, String description, int epicId) {
        super(id, name, status, description);
        this.epicId =  epicId;
    }

    public Subtask(int id, String name, State status, String description, int epicId,
                   Duration duration, LocalDateTime startTime) {
        super(id, name, status, description, duration, startTime);
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
        return String.format("(Id - '%d', Имя - '%s', Описание - '%s', статус - '%s', " +
                        "длительность - '%s', время начала - '%s', EpicId - '%d')",
                getId(), getName(), getDescription(), getStatus(), getDuration(), getStartTime(), getEpicId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Subtask subtask = (Subtask)obj;
        return Objects.equals(id, subtask.id) && Objects.equals(name, subtask.name) &&
                Objects.equals(description, subtask.description) && Objects.equals(status, subtask.status) &&
                Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(name, description, id, status, epicId);
    }
}
