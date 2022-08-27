import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.State;
import model.Subtask;
import model.Task;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();

        // Дебажим model.Task
        int taskId1 = inMemoryTaskManager.addTask(new Task("task 1", "description task 1"));
        int taskId2 = inMemoryTaskManager.addTask(new Task("task 2", "description task 2"));
        int taskId3 = inMemoryTaskManager.addTask(new Task("task 3", "description task 3"));
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

        // Дебажим model.Epic и model.Subtask
        int epicId1 = inMemoryTaskManager.addEpic(new Epic("epic 1", "description epic 1"));
        int epicId2 = inMemoryTaskManager.addEpic(new Epic("epic 2", "description epic 2"));
        int epicId3 = inMemoryTaskManager.addEpic(new Epic("epic 3", "description epic 3"));

        int subtaskId1 = inMemoryTaskManager.addSubtask(
                new Subtask("subtask 1", "description subtask 1", epicId1));
        int subtaskId2 = inMemoryTaskManager.addSubtask(
                new Subtask("subtask 2", "description subtask 2", epicId1));
        int subtaskId3 = inMemoryTaskManager.addSubtask(
                new Subtask("subtask 3", "description subtask 3", epicId2));
        int subtaskId4 = inMemoryTaskManager.addSubtask(
                new Subtask("subtask 4", "description subtask 4", epicId3));
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

        inMemoryTaskManager.addSubtask(new Subtask("subtask 5", "description subtask 5", epicId1));
        inMemoryTaskManager.addSubtask(new Subtask("subtask 6", "description subtask 6", epicId1));
        inMemoryTaskManager.removeAllEpics();

        System.out.println();

        // Дебажим историю (в списке уже есть 10 записей)
        inMemoryTaskManager.getEpic(inMemoryTaskManager.addEpic(new Epic("epic 4", "description epic 4")));
        inMemoryTaskManager.getEpic(inMemoryTaskManager.addEpic(new Epic("epic 5", "description epic 5")));
        List<Task> history =  inMemoryTaskManager.getHistory();

        System.out.println();
    }
}
