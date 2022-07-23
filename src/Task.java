public class Task {
    private String name;
    private String description;
    private String status;
    final static String[] STATE = new String[] {"NEW", "IN_PROGRESS", "DONE"};

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = STATE[0];
    }

    public Task(Task task, String status) {
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

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("(Имя - '%s', Описание - '%s', статус - '%s')", name, description, status);
    }
}
