public class Subtask extends Task{
    // Подзадача не может существовать без эпика
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask, String status) {
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