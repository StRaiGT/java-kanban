package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    // Эпик может существовать без подзадач
    private List<Integer> subtasksId = new ArrayList<>();
    private final TaskType taskType = TaskType.EPIC;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, Epic epic) {
        super(id, epic);
        this.subtasksId = epic.getSubtasksId();
    }

    public Epic(Epic epic, State status) {
        super(epic, status);
        this.subtasksId = epic.getSubtasksId();
    }

    public Epic(int id, String name, State status, String description, List subtasksId) {
        super(id, name, status, description);
        this.subtasksId = subtasksId;
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId(List<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    public void addSubtaskId(int id) {
        if (!subtasksId.contains(id)){
            subtasksId.add(id);
        }
    }

    public void removeSubtaskId(int id) {
        if (subtasksId.contains(id)){
            int index = 0;
            for (int i = 0; i < subtasksId.size(); i++) {
                if (subtasksId.get(i) == id) {
                    index = i;
                }
            }
            subtasksId.remove(index);
        }
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public String toString() {
        return String.format("(Id - '%d', Имя - '%s', Описание - '%s', статус - '%s', subtasksId - " + subtasksId + ")",
                getId(), getName(), getDescription(), getStatus());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Epic epic = (Epic)obj;
        return Objects.equals(id, epic.id) && Objects.equals(name, epic.name) &&
                Objects.equals(description, epic.description) && Objects.equals(status, epic.status) &&
                Objects.equals(subtasksId, epic.subtasksId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(name, description, id, status, subtasksId);
    }
}
