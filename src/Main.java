import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Дебагинг
        int task1 = taskManager.addTask("task 1", "description task 1");
        int task2 = taskManager.addTask("task 2", "description task 2");
        int epic1 = taskManager.addEpic("epic 1", "description epic 1");
        int subtask1 = taskManager.addSubtask("subtask 1", "description subtask 1", epic1);
        int subtask2 = taskManager.addSubtask("subtask 2", "description subtask 2", epic1);
        int epic2 = taskManager.addEpic("epic 2", "description epic 2");
        int subtask3 = taskManager.addSubtask("subtask 3", "description subtask 3", epic2);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        taskManager.updateTask(task1, new Task(
                new Task("task 1 update", "description task 1 update"), Task.STATE[1]));
        taskManager.updateTask(task2, new Task(taskManager.getTask(task2), Task.STATE[1]));
        taskManager.updateEpic(epic1, new Epic("epic 1 update", "description epic 1 update"));
        taskManager.updateSubtask(subtask1, new Subtask(
                new Subtask("subtask 1 update", "description subtask 1 update", epic1), Task.STATE[1]));
        taskManager.updateSubtask(subtask2, new Subtask(taskManager.getSubtask(subtask2), Task.STATE[2]));
        taskManager.updateEpic(epic2, new Epic("epic 2 update", "description epic 2 update"));
        taskManager.updateSubtask(subtask3, new Subtask(taskManager.getSubtask(subtask3), Task.STATE[2]));

        System.out.println();
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        taskManager.removeEpic(epic1);
        taskManager.removeSubtask(subtask3);

        System.out.println();
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        System.out.println();
    }
}
