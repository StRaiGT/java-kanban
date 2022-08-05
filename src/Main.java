public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();

        // Дебажим Task
        Task task1 = new Task("task 1", "description task 1");
        Task task2 = new Task("task 2", "description task 2");
        Task task3 = new Task("task 3", "description task 3");
        int taskId1 = inMemoryTaskManager.addTask(task1);
        int taskId2 = inMemoryTaskManager.addTask(task2);
        int taskId3 = inMemoryTaskManager.addTask(task3);
        System.out.println(inMemoryTaskManager.getTasks());

        System.out.println(inMemoryTaskManager.getTask(taskId2));

        inMemoryTaskManager.updateTask(taskId1, new Task(
                new Task("task 1 update", "description task 1 update"), State.IN_PROGRESS));
        inMemoryTaskManager.updateTask(taskId2, new Task(
                new Task("task 2 update", "description task 2 update"),
                inMemoryTaskManager.getTask(taskId2).getStatus()));
        inMemoryTaskManager.updateTask(taskId3, new Task(inMemoryTaskManager.getTask(taskId3), State.DONE));

        inMemoryTaskManager.removeTask(taskId2);

        inMemoryTaskManager.removeAllTasks();

        System.out.println();

        // Дебажим Epic и Subtask
        Epic epic1 = new Epic("epic 1", "description epic 1");
        Epic epic2 = new Epic("epic 2", "description epic 2");
        Epic epic3 = new Epic("epic 3", "description epic 3");
        int epicId1 = inMemoryTaskManager.addEpic(epic1);
        int epicId2 = inMemoryTaskManager.addEpic(epic2);
        int epicId3 = inMemoryTaskManager.addEpic(epic3);
        Subtask subtask1 = new Subtask("subtask 1", "description subtask 1", epicId1);
        Subtask subtask2 = new Subtask("subtask 2", "description subtask 2", epicId1);
        Subtask subtask3 = new Subtask("subtask 3", "description subtask 3", epicId2);
        Subtask subtask4 = new Subtask("subtask 4", "description subtask 4", epicId3);
        int subtaskId1 = inMemoryTaskManager.addSubtask(subtask1);
        int subtaskId2 = inMemoryTaskManager.addSubtask(subtask2);
        int subtaskId3 = inMemoryTaskManager.addSubtask(subtask3);
        int subtaskId4 = inMemoryTaskManager.addSubtask(subtask4);
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(inMemoryTaskManager.getSubtasks());

        System.out.println(inMemoryTaskManager.getEpic(epicId1));
        System.out.println(inMemoryTaskManager.getSubtask(subtaskId4));

        inMemoryTaskManager.updateEpic(epicId1, new Epic("epic 1 update", "description epic 1 update"));
        inMemoryTaskManager.updateSubtask(subtaskId1, new Subtask(
                new Subtask("subtask 1 update", "description subtask 1 update",
                        inMemoryTaskManager.getSubtask(subtaskId1).getEpicId()), State.IN_PROGRESS));
        inMemoryTaskManager.updateSubtask(subtaskId2, new Subtask(inMemoryTaskManager.getSubtask(subtaskId2), State.DONE));
        inMemoryTaskManager.updateSubtask(subtaskId3, new Subtask(
                new Subtask("subtask 3 update", "description subtask 3 update",
                        inMemoryTaskManager.getSubtask(subtaskId3).getEpicId()),
                inMemoryTaskManager.getSubtask(subtaskId3).getStatus()));
        inMemoryTaskManager.updateSubtask(subtaskId4, new Subtask(inMemoryTaskManager.getSubtask(subtaskId4), State.DONE));

        inMemoryTaskManager.removeSubtask(subtaskId4);

        inMemoryTaskManager.removeEpic(epicId2);

        inMemoryTaskManager.removeAllSubtasks();

        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);
        inMemoryTaskManager.removeAllEpics();

        System.out.println();

        // Дебажим историю (в списке уже есть 10 записей)
        inMemoryTaskManager.getEpic(inMemoryTaskManager.addEpic(new Epic("epic 4", "description epic 4")));
        inMemoryTaskManager.getEpic(inMemoryTaskManager.addEpic(new Epic("epic 5", "description epic 5")));
        inMemoryTaskManager.getHistory();

        System.out.println();
    }
}
