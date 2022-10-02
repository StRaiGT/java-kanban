package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    protected int id;
    protected final String name;
    protected final String description;
    protected final State status;
    protected final TaskType taskType = TaskType.TASK;
    protected Duration duration;
    protected LocalDateTime startTime;

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
        this.startTime = task.getStartTime();
        this.duration = task.getDuration();
    }

    public Task(Task task, State status) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = status;
        this.startTime = task.getStartTime();
        this.duration = task.getDuration();
    }

    public Task(Task task, Duration duration, LocalDateTime startTime) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String name, State status, String description) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public Task(int id, String name, State status, String description, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
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

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("(Id - '%d', Имя - '%s', Описание - '%s', статус - '%s', " +
                        "длительность - '%s', время начала - '%s')",
                getId(), getName(), getDescription(), getStatus(), getDuration(), getStartTime());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task task = (Task)obj;
        return Objects.equals(id, task.id) && Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode(){
        return Objects.hash(name, description, id, status);
    }

    @Override
    public int compareTo(Task obj) {
        LocalDateTime time1 = this.getStartTime();
        LocalDateTime time2 = obj.getStartTime();
        if (time1 == null){
            return 1;
        }
        if (time2 == null){
            return -1;
        }
        return time1.compareTo(time2);
    }
}
