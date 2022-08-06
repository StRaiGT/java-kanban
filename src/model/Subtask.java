package model;

public class Subtask extends Task {
    // Подзадача не может существовать без эпика
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask, State status) {
        super(subtask, status);
        this.epicId = subtask.getEpicId();
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format("(Имя - '%s', Описание - '%s', статус - '%s', EpicId - '%d')",
                getName(), getDescription(), getStatus(), epicId);
    }
}
