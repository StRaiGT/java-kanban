import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Дебажим Task
        Task task1 = new Task("task 1", "description task 1");
        Task task2 = new Task("task 2", "description task 2");
        Task task3 = new Task("task 3", "description task 3");
        int taskId1 = taskManager.addTask(task1);
        int taskId2 = taskManager.addTask(task2);
        int taskId3 = taskManager.addTask(task3);
        System.out.println(taskManager.getTasks());

        System.out.println(taskManager.getTask(taskId2));

        taskManager.updateTask(taskId1, new Task(
                new Task("task 1 update", "description task 1 update"), Task.STATE[1]));
        taskManager.updateTask(taskId2, new Task(
                new Task("task 2 update", "description task 2 update"),
                taskManager.getTask(taskId2).getStatus()));
        taskManager.updateTask(taskId3, new Task(taskManager.getTask(taskId3), Task.STATE[2]));

        taskManager.removeTask(taskId2);

        taskManager.removeAllTasks();

        System.out.println();

        // Дебажим Epic и Subtask
        Epic epic1 = new Epic("epic 1", "description epic 1");
        Epic epic2 = new Epic("epic 2", "description epic 2");
        Epic epic3 = new Epic("epic 3", "description epic 3");
        int epicId1 = taskManager.addEpic(epic1);
        int epicId2 = taskManager.addEpic(epic2);
        int epicId3 = taskManager.addEpic(epic3);
        Subtask subtask1 = new Subtask("subtask 1", "description subtask 1", epicId1);
        Subtask subtask2 = new Subtask("subtask 2", "description subtask 2", epicId1);
        Subtask subtask3 = new Subtask("subtask 3", "description subtask 3", epicId2);
        Subtask subtask4 = new Subtask("subtask 4", "description subtask 4", epicId3);
        int subtaskId1 = taskManager.addSubtask(subtask1);
        int subtaskId2 = taskManager.addSubtask(subtask2);
        int subtaskId3 = taskManager.addSubtask(subtask3);
        int subtaskId4 = taskManager.addSubtask(subtask4);
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        System.out.println(taskManager.getEpic(epicId1));
        System.out.println(taskManager.getSubtask(subtaskId4));

        taskManager.updateEpic(epicId1, new Epic("epic 1 update", "description epic 1 update"));
        taskManager.updateSubtask(subtaskId1, new Subtask(
                new Subtask("subtask 1 update", "description subtask 1 update",
                        taskManager.getSubtask(subtaskId1).getEpicId()), Task.STATE[1]));
        taskManager.updateSubtask(subtaskId2, new Subtask(taskManager.getSubtask(subtaskId2), Task.STATE[2]));
        taskManager.updateSubtask(subtaskId3, new Subtask(
                new Subtask("subtask 3 update", "description subtask 3 update",
                        taskManager.getSubtask(subtaskId3).getEpicId()),
                taskManager.getSubtask(subtaskId3).getStatus()));
        taskManager.updateSubtask(subtaskId4, new Subtask(taskManager.getSubtask(subtaskId4), Task.STATE[2]));

        taskManager.removeSubtask(subtaskId4);

        taskManager.removeEpic(epicId2);

        taskManager.removeAllSubtasks();

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.removeAllEpics();

        System.out.println();
    }
}
