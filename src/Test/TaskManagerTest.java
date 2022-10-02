package Test;

import manager.TaskManager;
import model.Epic;
import model.State;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager>{
    protected T manager;
    public TaskManagerTest(T manager) {
        this.manager = manager;
    }

    @BeforeEach
    public void cleanManager() {
        manager.removeAllTasks();
        manager.removeAllEpics();
    }

    @Test
    public void shouldAddTaskIfNotNull() {
        int taskId1 = manager.addTask(new Task("task 1", "description task 1"));
        assertEquals(1, taskId1);
    }

    @Test
    public void shouldNotAddTaskIfNull() {
        manager.addTask(null);
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    public void shouldAddEpicIfNotNull() {
        int epicId = manager.addEpic(new Epic("epic 1", "description epic 1"));
        assertEquals(1, epicId);
    }

    @Test
    public void shouldNotAddEpicIfNull() {
        manager.addEpic(null);
        assertEquals(0, manager.getEpics().size());
    }


    @Test
    public void shouldAddSubtaskIfNotNull() {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        int subtaskId1 = manager.addSubtask(new Subtask(
                new Subtask("subtask 1", "description subtask 1", epicId1),
                State.DONE));
        assertEquals(2, subtaskId1);
        assertEquals(manager.getSubtask(subtaskId1).getEpicId(), epicId1);
        assertEquals(State.DONE, manager.getEpic(epicId1).getStatus());
    }

    @Test
    public void shouldNotAddSubtaskIfNull() {
        manager.addSubtask(null);
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    public void shouldNotAddSubtaskIfNoParentEpic() {
        int subtaskId1 = manager.addSubtask(new Subtask("subtask 1", "description subtask 1", 100));
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    public void shouldReturnNotEmptyListIfHasTasks() {
        Task task1 = new Task(1, "task 1", State.NEW, "description task 1");
        Task task2 = new Task(2, "task 2", State.NEW, "description task 2");
        manager.addTask(task1);
        manager.addTask(task2);
        List<Task> tasks = manager.getTasks();
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
        assertEquals(2, tasks.size());
    }

    @Test
    public void shouldReturnEmptyListIfNoTasks() {
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    public void shouldReturnNotEmptyListIfHasEpics() {
        Epic epic1 = new Epic(1, "epic 1", State.NEW, "description epic 1", null);
        Epic epic2 = new Epic(2, "epic 2", State.NEW, "description epic 2", null);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        List<Epic> epics = manager.getEpics();
        assertTrue(epics.contains(epic1));
        assertTrue(epics.contains(epic2));
        assertEquals(2, epics.size());
    }

    @Test
    public void shouldReturnEmptyListIfNoEpics() {
        assertTrue(manager.getEpics().isEmpty());
    }

    @Test
    public void shouldReturnNotEmptyListIfHasSubtasks() {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        Subtask subtask = new Subtask(2, "subtask 1", State.NEW, "description subtask 1", epicId1);
        manager.addSubtask(subtask);
        List<Subtask> subtasks = manager.getSubtasks();
        assertTrue(subtasks.contains(subtask));
        assertEquals(1, subtasks.size());
    }

    @Test
    public void shouldReturnEmptyArrayIfNoSubtasks() {
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    public void shouldReturnNullIfTaskNotExist() {
        assertNull(manager.getTask(22222));
    }
    @Test
    public void shouldReturnNullIfEpicNotExist() {
        assertNull(manager.getEpic(22222));
    }

    @Test
    public void shouldReturnNullIfSubtaskNotExist() {
        assertNull(manager.getSubtask(22222));
    }

    @Test
    public void shouldRemoveTaskIfContains() {
        int taskId1 = manager.addTask(new Task("task 1", "description task 1"));
        manager.removeTask(taskId1);
        assertNull(manager.getTask(taskId1));
    }

    @Test
    public void shouldNotChangeTasksMapIfNotContains() {
        Task task1 = new Task(1, "task 1", State.NEW, "description task 1");
        Task task2 = new Task(2, "task 1", State.NEW, "description task 1");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.removeTask(222222);
        List<Task> tasks = manager.getTasks();
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
        assertEquals(2, tasks.size());
    }

    @Test
    public void shouldDoNothingIfTasksMapIsEmpty() {
        manager.removeAllTasks();
        manager.removeTask(222222);
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    public void shouldRemoveEpicIfContains() {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        manager.removeEpic(epicId1);
        assertNull(manager.getEpic(epicId1));
    }

    @Test
    public void shouldNotChangeEpicsMapIfNotContains() {
        Epic epic1 = new Epic(1, "epic 1", State.NEW, "description epic 1", null);
        Epic epic2 = new Epic(2, "epic 2", State.NEW, "description epic 2", null);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.removeEpic(222222);
        List<Epic> epics = manager.getEpics();
        assertTrue(epics.contains(epic1));
        assertTrue(epics.contains(epic2));
        assertEquals(2, epics.size());
    }

    @Test
    public void shouldDoNothingIfEpicsMapIsEmpty() {
        manager.removeAllEpics();
        manager.removeEpic(222222);
        assertTrue(manager.getEpics().isEmpty());
    }

    @Test
    public void shouldRemoveSubtaskIfContains() {
        Epic epic1 = new Epic("epic 1", "description epic 1");
        int epicId1 = manager.addEpic(epic1);
        int subtaskId1 = manager.addSubtask(new Subtask("test", "test", epicId1));
        manager.removeSubtask(subtaskId1);
        assertNull(manager.getSubtask(subtaskId1));
        assertEquals(0, manager.getEpic(epicId1).getSubtasksId().size());
    }

    @Test
    public void shouldNotChangeSubtasksMapIfNotContains() {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        int epicId2 = manager.addEpic(new Epic("epic 2", "description epic 2"));
        Subtask subtask1 = new Subtask(3, "subtask 1", State.NEW, "description subtask 1", epicId1);
        Subtask subtask2 = new Subtask(4, "subtask 2", State.NEW, "description subtask 2", epicId2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.removeSubtask(2222222);
        List<Subtask> subtasks = manager.getSubtasks();
        assertTrue(subtasks.contains(subtask1));
        assertTrue(subtasks.contains(subtask2));
        assertEquals(2, subtasks.size());
    }

    @Test
    public void shouldDoNothingIfSubtasksMapIsEmpty() {
        manager.removeAllEpics();
        manager.removeSubtask(222222);
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    public void shouldUpdateTaskIfNotNull() {
        Task task1 = new Task(1, "task 1", State.NEW, "description task 1");
        int taskId1 = manager.addTask(task1);
        Task task2 = new Task(1, "task 2", State.NEW, "description task 2");
        manager.updateTask(taskId1, task2);
        assertEquals(task2, manager.getTask(taskId1));
    }

    @Test
    public void shouldNotUpdateTaskIfNull() {
        Task task1 = new Task(1, "task 1", State.NEW, "description task 1");
        int taskId1 = manager.addTask(task1);
        manager.updateTask(taskId1, null);
        assertEquals(task1, manager.getTask(taskId1));
    }

    @Test
    public void shouldUpdateEpicIfNotNull() {
        Epic epic1 = new Epic(1, "epic 1", State.NEW, "description epic 1", null);
        int epicId1 = manager.addEpic(epic1);
        Epic epic2 = new Epic(1, "epic 2", State.NEW, "description epic 2", null);
        manager.updateEpic(epicId1, epic2);
        assertEquals(epic2, manager.getEpic(epicId1));
    }

    @Test
    public void shouldNotUpdateEpicIfNull() {
        Epic epic1 = new Epic(1, "epic 1", State.NEW, "description epic 1", null);
        int epicId1 = manager.addEpic(epic1);
        manager.updateEpic(epicId1, null);
        assertEquals(epic1, manager.getEpic(epicId1));
    }

    @Test
    public void shouldUpdateSubtaskIfNotNull(){
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        Subtask subtask1 = new Subtask(2, "subtask 1", State.NEW, "description subtask 1", epicId1);
        Subtask subtask2 = new Subtask(2, "subtask 2", State.DONE, "description subtask 2", epicId1);
        int subtaskId1 = manager.addSubtask(subtask1);
        manager.updateSubtask(subtaskId1, subtask2);
        assertEquals(subtask2, manager.getSubtask(subtaskId1));
    }

    @Test
    public void shouldNotUpdateSubtaskIfNull(){
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        Subtask subtask1 = new Subtask(2, "subtask 1", State.NEW, "description subtask 1", epicId1);
        int subtaskId1 = manager.addSubtask(subtask1);
        manager.updateSubtask(subtaskId1, null);
        assertEquals(subtask1, manager.getSubtask(subtaskId1));
    }

    @Test
    public void shouldReturnEmptySubtasksListIfEpicHasNotSubtasks() {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        assertTrue(manager.getEpicSubtasks(epicId1).isEmpty());
    }

     @Test
    public void shouldReturnNullIfEpicDoesNotExist() {
        assertNull(manager.getEpicSubtasks(22222));
    }


    @Test
    public void shouldReturnNotEmptyListIfEpicHavesSubtasks() {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        Subtask subtask = new Subtask(2, "subtask 1", State.NEW, "description subtask 1", epicId1);
        int subtaskId1 = manager.addSubtask(subtask);
        Map<Integer, Subtask> epicSubtasks = manager.getEpicSubtasks(epicId1);
        assertTrue(epicSubtasks.containsKey(subtaskId1));
        assertEquals(1, epicSubtasks.size());
    }

    @Test
    public void epicsShouldNotHaveSubtasksAfterCleaningSubtasksMap() {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        int epicId2 = manager.addEpic(new Epic("epic 2", "description epic 2"));
        Subtask subtask1 = new Subtask(3, "subtask 1", State.NEW, "description subtask 1", epicId1);
        Subtask subtask2 = new Subtask(4, "subtask 2", State.NEW, "description subtask 2", epicId2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.removeAllSubtasks();
        assertTrue(manager.getEpic(epicId1).getSubtasksId().isEmpty());
        assertTrue(manager.getEpic(epicId2).getSubtasksId().isEmpty());
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    public void shouldReturnHistoryWithTasks() {
        Task task = new Task("task 1", "description task 1");
        int taskId = manager.addTask(task);
        Epic epic = new Epic("epic 1", "description epic 1");
        int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("subtask 1", "description subtask 1", epicId);
        int subtaskId = manager.addSubtask(subtask);

        manager.getTask(taskId);
        manager.getEpic(epicId);
        manager.getSubtask(subtaskId);
        List<Task> tasks = manager.getHistory();
        assertEquals(3, tasks.size());
    }

    @Test
    public void shouldReturnEmptyHistoryIfManagerHasNoTasks() {
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldReturnEmptyHistoryIfTasksNotExist() {
        manager.getTask(222222);
        manager.getSubtask(222222);
        manager.getEpic(2222222);
        assertTrue(manager.getHistory().isEmpty());
    }
}