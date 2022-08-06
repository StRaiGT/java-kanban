package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    // Эпик может существовать без подзадач
    private List<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(Epic epic, State status) {
        super(epic, status);
        this.subtasksId = epic.getSubtasksId();
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
    public String toString() {
        return String.format("(Имя - '%s', Описание - '%s', статус - '%s', subtasksId - " + subtasksId + ")",
                getName(), getDescription(), getStatus());
    }
}