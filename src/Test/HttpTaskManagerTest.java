package Test;

import manager.TaskManager;
import model.Epic;
import model.State;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;
import server.KVServer;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest {
    private static KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private HttpTaskServer newHttpTaskServer;
    private TaskManager manager;
    private TaskManager newManager;

    private void changeHttpServer() throws IOException {
        httpTaskServer.stop();
        newHttpTaskServer = new HttpTaskServer();
        newHttpTaskServer.start();
        newManager = newHttpTaskServer.getTaskManager();
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @BeforeEach
    void beforeEach() throws IOException {
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        manager = httpTaskServer.getTaskManager();
        manager.removeAllTasks();
        manager.removeAllEpics();
        manager.removeAllSubtasks();
    }

    @AfterAll
    static void afterAll() {
        kvServer.stop();
    }

    @AfterEach
    void afterEach() {
        newHttpTaskServer.stop();
    }

    @Test
    public void shouldAddTaskAndLoadFromServer() throws IOException {
        int taskId1 = manager.addTask(new Task(0, "task 1", State.NEW, "description task 1",
                Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 6, 30)));
        int taskId2 = manager.addTask(new Task(1, "task 2", State.NEW, "description task 2",
                Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 9, 30)));

        changeHttpServer();

        Task loadedTask = newManager.getTask(taskId1);

        assertNotNull(loadedTask);
        assertEquals(2, newManager.getTasks().size());
        assertEquals("description task 1", loadedTask.getDescription());
        assertEquals(State.NEW, loadedTask.getStatus());
        assertEquals("2003-01-28T06:30", loadedTask.getStartTime().toString());
        assertEquals(30, loadedTask.getDuration().toMinutes());
    }

    @Test
    public void shouldNotAddTaskIfNullAndLoadFromServer() throws IOException {
        int taskId1 = manager.addTask(null);

        changeHttpServer();

        Task loadedTask = newManager.getTask(taskId1);

        assertNull(loadedTask);
        assertEquals(0, newManager.getTasks().size());
    }

    @Test
    public void shouldAddEpicAndLoadFromServer() throws IOException {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        int epicId2 = manager.addEpic(new Epic("epic 2", "description epic 2"));

        changeHttpServer();

        Epic loadedEpic = newManager.getEpic(epicId1);

        assertNotNull(loadedEpic);
        assertEquals(2, newManager.getEpics().size());
        assertEquals("description epic 1", loadedEpic.getDescription());
        assertEquals(State.NEW, loadedEpic.getStatus());
    }

    @Test
    public void shouldNotAddEpicIfNulAndLoadFromServer() throws IOException {
        int epicId1 = manager.addEpic(null);

        changeHttpServer();

        Epic loadedEpic = newManager.getEpic(epicId1);

        assertNull(loadedEpic);
        assertEquals(0, newManager.getEpics().size());
    }

    @Test
    public void shouldAddSubtaskAndLoadFromServer() throws IOException {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        int subtaskId1 = manager.addSubtask(new Subtask(0, "subtask 1", State.NEW, "description subtask 1", epicId1,
                Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 6, 30)));

        changeHttpServer();

        Subtask loadedSubtask = newManager.getSubtask(subtaskId1);

        assertNotNull(loadedSubtask);
        assertEquals(1, newManager.getSubtasks().size());
        assertEquals("description subtask 1", loadedSubtask.getDescription());
        assertEquals(State.NEW, loadedSubtask.getStatus());
        assertEquals("2003-01-28T06:30", loadedSubtask.getStartTime().toString());
        assertEquals(30, loadedSubtask.getDuration().toMinutes());
    }

    @Test
    public void shouldNotAddSubtaskIfNullAndLoadFromServer() throws IOException {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        int subtaskId1 = manager.addSubtask(null);

        changeHttpServer();

        Subtask loadedSubtask = newManager.getSubtask(subtaskId1);

        assertNull(loadedSubtask);
        assertEquals(0, newManager.getSubtasks().size());
    }

    @Test
    public void shouldNotAddSubtaskIfNoParentEpicAndLoadFromServer() throws IOException {
        int subtaskId1 = manager.addSubtask(new Subtask(0, "subtask 1", State.NEW, "description subtask 1", 100,
                Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 6, 30)));

        changeHttpServer();

        Subtask loadedSubtask = newManager.getSubtask(subtaskId1);

        assertNull(loadedSubtask);
        assertEquals(0, newManager.getSubtasks().size());
    }

    @Test
    public void shouldReturnNotEmptyListIfHasTasksAfterLoadFromServer() throws IOException {
        Task task1 = new Task(1, "task 1", State.NEW, "description task 1");
        Task task2 = new Task(2, "task 2", State.NEW, "description task 2");
        manager.addTask(task1);
        manager.addTask(task2);

        changeHttpServer();

        List<Task> tasks = newManager.getTasks();

        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
        assertEquals(2, tasks.size());
    }

    @Test
    public void shouldReturnEmptyListIfNoTasksAfterLoadFromServer() throws IOException {
        changeHttpServer();

        assertTrue(newManager.getTasks().isEmpty());
    }

    @Test
    public void shouldReturnNotEmptyListIfHasEpicsAfterLoadFromServer() throws IOException {
        Epic epic1 = new Epic(1, "epic 1", State.NEW, "description epic 1", null);
        Epic epic2 = new Epic(2, "epic 2", State.NEW, "description epic 2", null);
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        changeHttpServer();

        List<Epic> epics = newManager.getEpics();

        assertTrue(epics.contains(epic1));
        assertTrue(epics.contains(epic2));
        assertEquals(2, epics.size());
    }

    @Test
    public void shouldReturnEmptyListIfNoEpicsAfterLoadFromServer() throws IOException {
        changeHttpServer();

        assertTrue(newManager.getEpics().isEmpty());
    }

    @Test
    public void shouldReturnNotEmptyListIfHasSubtasksAfterLoadFromServer() throws IOException {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        Subtask subtask = new Subtask(2, "subtask 1", State.NEW, "description subtask 1", epicId1);
        manager.addSubtask(subtask);

        changeHttpServer();

        List<Subtask> subtasks = newManager.getSubtasks();

        assertTrue(subtasks.contains(subtask));
        assertEquals(1, subtasks.size());
    }

    @Test
    public void shouldReturnEmptyArrayIfNoSubtasksAfterLoadFromServer() throws IOException {
        changeHttpServer();

        assertTrue(newManager.getSubtasks().isEmpty());
    }

    @Test
    public void shouldReturnNullIfTaskNotExistAfterLoadFromServer() throws IOException {
        changeHttpServer();

        assertNull(newManager.getTask(22222));
    }

    @Test
    public void shouldReturnNullIfEpicNotExistAfterLoadFromServer() throws IOException {
        changeHttpServer();

        assertNull(newManager.getEpic(22222));
    }

    @Test
    public void shouldReturnNullIfSubtaskNotExistAfterLoadFromServer() throws IOException {
        changeHttpServer();

        assertNull(newManager.getSubtask(22222));
    }

    @Test
    public void shouldRemoveTaskIfContainsAndLoadFromServer() throws IOException {
        int taskId1 = manager.addTask(new Task("task 1", "description task 1"));
        manager.removeTask(taskId1);

        changeHttpServer();

        assertNull(newManager.getTask(taskId1));
    }

    @Test
    public void shouldNotChangeTasksMapIfNotContainsAndLoadFromServer() throws IOException {
        Task task1 = new Task(1, "task 1", State.NEW, "description task 1");
        Task task2 = new Task(2, "task 1", State.NEW, "description task 1");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.removeTask(222222);

        changeHttpServer();

        List<Task> tasks = newManager.getTasks();

        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
        assertEquals(2, tasks.size());
    }

    @Test
    public void shouldDoNothingIfTasksMapIsEmptyAndLoadFromServer() throws IOException {
        manager.removeAllTasks();
        manager.removeTask(222222);

        changeHttpServer();

        assertEquals(0, newManager.getTasks().size());
    }

    @Test
    public void shouldRemoveEpicIfContainsAndLoadFromServer() throws IOException {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        manager.removeEpic(epicId1);

        changeHttpServer();

        assertNull(newManager.getEpic(epicId1));
    }

    @Test
    public void shouldNotChangeEpicsMapIfNotContainsAndLoadFromServer() throws IOException {
        Epic epic1 = new Epic(1, "epic 1", State.NEW, "description epic 1", null);
        Epic epic2 = new Epic(2, "epic 2", State.NEW, "description epic 2", null);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.removeEpic(222222);

        changeHttpServer();

        List<Epic> epics = newManager.getEpics();

        assertTrue(epics.contains(epic1));
        assertTrue(epics.contains(epic2));
        assertEquals(2, epics.size());
    }

    @Test
    public void shouldDoNothingIfEpicsMapIsEmptyAndLoadFromServer() throws IOException {
        manager.removeAllEpics();
        manager.removeEpic(222222);

        changeHttpServer();

        assertTrue(newManager.getEpics().isEmpty());
    }

    @Test
    public void shouldRemoveSubtaskIfContainsAndLoadFromServer() throws IOException {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        int subtaskId1 = manager.addSubtask(new Subtask("test", "test", epicId1));
        manager.removeSubtask(subtaskId1);

        changeHttpServer();

        assertNull(newManager.getSubtask(subtaskId1));
        assertEquals(0, newManager.getEpic(epicId1).getSubtasksId().size());
    }

    @Test
    public void shouldNotChangeSubtasksMapIfNotContainsAndLoadFromServer() throws IOException {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        int epicId2 = manager.addEpic(new Epic("epic 2", "description epic 2"));
        Subtask subtask1 = new Subtask(3, "subtask 1", State.NEW, "description subtask 1", epicId1);
        Subtask subtask2 = new Subtask(4, "subtask 2", State.NEW, "description subtask 2", epicId2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.removeSubtask(2222222);

        changeHttpServer();

        List<Subtask> subtasks = newManager.getSubtasks();

        assertTrue(subtasks.contains(subtask1));
        assertTrue(subtasks.contains(subtask2));
        assertEquals(2, subtasks.size());
    }

    @Test
    public void shouldDoNothingIfSubtasksMapIsEmptyAndLoadFromServer() throws IOException {
        manager.removeAllEpics();
        manager.removeSubtask(222222);

        changeHttpServer();

        assertEquals(0, newManager.getSubtasks().size());
    }

    @Test
    public void shouldUpdateTaskIfNotNullAndLoadFromServer() throws IOException {
        Task task1 = new Task(1, "task 1", State.NEW, "description task 1");
        int taskId1 = manager.addTask(task1);
        Task task2 = new Task(1, "task 2", State.NEW, "description task 2");
        manager.updateTask(taskId1, task2);

        changeHttpServer();

        assertEquals(task2, newManager.getTask(taskId1));
    }

    @Test
    public void shouldNotUpdateTaskIfNullAndLoadFromServer() throws IOException {
        Task task1 = new Task(1, "task 1", State.NEW, "description task 1");
        int taskId1 = manager.addTask(task1);
        manager.updateTask(taskId1, null);

        changeHttpServer();

        assertEquals(task1, newManager.getTask(taskId1));
    }

    @Test
    public void shouldUpdateEpicIfNotNullAndLoadFromServer() throws IOException {
        Epic epic1 = new Epic(1, "epic 1", State.NEW, "description epic 1", null);
        int epicId1 = manager.addEpic(epic1);
        Epic epic2 = new Epic(1, "epic 2", State.NEW, "description epic 2", null);
        manager.updateEpic(epicId1, epic2);

        changeHttpServer();

        assertEquals(epic2, newManager.getEpic(epicId1));
    }

    @Test
    public void shouldNotUpdateEpicIfNullAndLoadFromServer() throws IOException {
        Epic epic1 = new Epic(1, "epic 1", State.NEW, "description epic 1", null);
        int epicId1 = manager.addEpic(epic1);
        manager.updateEpic(epicId1, null);

        changeHttpServer();

        assertEquals(epic1, newManager.getEpic(epicId1));
    }

    @Test
    public void shouldUpdateSubtaskIfNotNullAndLoadFromServer() throws IOException {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        Subtask subtask1 = new Subtask(2, "subtask 1", State.NEW, "description subtask 1", epicId1);
        Subtask subtask2 = new Subtask(2, "subtask 2", State.DONE, "description subtask 2", epicId1);
        int subtaskId1 = manager.addSubtask(subtask1);
        manager.updateSubtask(subtaskId1, subtask2);

        changeHttpServer();

        assertEquals(subtask2, newManager.getSubtask(subtaskId1));
    }

    @Test
    public void shouldNotUpdateSubtaskIfNullAndLoadFromServer() throws IOException {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        Subtask subtask1 = new Subtask(2, "subtask 1", State.NEW, "description subtask 1", epicId1);
        int subtaskId1 = manager.addSubtask(subtask1);
        manager.updateSubtask(subtaskId1, null);

        changeHttpServer();

        assertEquals(subtask1, newManager.getSubtask(subtaskId1));
    }

    @Test
    public void shouldReturnEmptySubtasksListIfEpicHasNotSubtasksAfterLoadFromServer() throws IOException {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));

        changeHttpServer();

        assertTrue(newManager.getEpicSubtasks(epicId1).isEmpty());
    }

    @Test
    public void shouldReturnNullIfEpicDoesNotExistAfterLoadFromServer() throws IOException {
        changeHttpServer();

        assertNull(newManager.getEpicSubtasks(22222));
    }

    @Test
    public void shouldReturnNotEmptyListIfEpicHavesSubtasksAndLoadFromServer() throws IOException {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        Subtask subtask = new Subtask(2, "subtask 1", State.NEW, "description subtask 1", epicId1);
        int subtaskId1 = manager.addSubtask(subtask);

        changeHttpServer();

        Map<Integer, Subtask> epicSubtasks = newManager.getEpicSubtasks(epicId1);

        assertTrue(epicSubtasks.containsKey(subtaskId1));
        assertEquals(1, epicSubtasks.size());
    }

    @Test
    public void epicsShouldNotHaveSubtasksAfterCleaningSubtasksMapAndLoadFromServer() throws IOException {
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        int epicId2 = manager.addEpic(new Epic("epic 2", "description epic 2"));
        Subtask subtask1 = new Subtask(3, "subtask 1", State.NEW, "description subtask 1", epicId1);
        Subtask subtask2 = new Subtask(4, "subtask 2", State.NEW, "description subtask 2", epicId2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.removeAllSubtasks();

        changeHttpServer();

        assertTrue(newManager.getEpic(epicId1).getSubtasksId().isEmpty());
        assertTrue(newManager.getEpic(epicId2).getSubtasksId().isEmpty());
        assertTrue(newManager.getSubtasks().isEmpty());
    }

    @Test
    public void shouldReturnHistoryWithTasksAfterLoadFromServer() throws IOException {
        Task task = new Task("task 1", "description task 1");
        int taskId = manager.addTask(task);
        Epic epic = new Epic("epic 1", "description epic 1");
        int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("subtask 1", "description subtask 1", epicId);
        int subtaskId = manager.addSubtask(subtask);
        manager.getTask(taskId);
        manager.getEpic(epicId);
        manager.getSubtask(subtaskId);

        changeHttpServer();

        List<Task> tasks = newManager.getHistory();

        assertEquals(3, tasks.size());
    }

    @Test
    public void shouldReturnEmptyHistoryIfManagerHasNoTasksAfterLoadFromServer() throws IOException {
        changeHttpServer();

        assertTrue(newManager.getHistory().isEmpty());
    }

    @Test
    public void shouldReturnEmptyHistoryIfTasksNotExistAfterLoadFromServer() throws IOException {
        manager.getTask(222222);
        manager.getSubtask(222222);
        manager.getEpic(2222222);

        changeHttpServer();

        assertTrue(newManager.getHistory().isEmpty());
    }

    @Test
    public void shouldNotMakeTaskIfItHasIntersectionAndLoadFromServer() throws IOException {
        LocalDateTime start = LocalDateTime.of(2003, 1, 28, 9, 30);
        Duration duration = Duration.ofMinutes(30);
        Task task1 = new Task(1, "task 1", State.NEW, "description task 1", duration, start);
        Task task2 = new Task(2, "task 2", State.NEW, "description task 2", null, null);
        Task task3 = new Task(3, "task 3", State.NEW, "description task 3", duration, start);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        changeHttpServer();

        List<Task> tasks = newManager.getTasks();

        assertEquals(3, task3.getId());
        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
        assertFalse(tasks.contains(task3));
    }

    @Test
    public void shouldMakeTasksIfNoIntersectionAndLoadFromServer() throws IOException {
        LocalDateTime start = LocalDateTime.of(2003, 1, 28, 9, 30);
        Duration duration = Duration.ofMinutes(30);
        Task task1 = new Task(1, "task 1", State.NEW, "description task 1", duration, start);
        Task task2 = new Task(2, "task 2", State.NEW, "description task 2", duration, start.plusDays(1));
        manager.addTask(task1);
        manager.addTask(task2);

        changeHttpServer();

        List<Task> tasks = newManager.getTasks();

        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
    }

    @Test
    public void shouldMakeTasksIfTimeIsNullAndLoadFromServer() throws IOException {
        Task task1 = new Task(1, "task 1", State.NEW, "description task 1",  null, null);
        Task task2 = new Task(2, "task 2", State.NEW, "description task 2", null, null);
        manager.addTask(task1);
        manager.addTask(task2);

        changeHttpServer();

        List<Task> tasks = newManager.getTasks();

        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
    }

    @Test
    public void returnEmptySortedTasksArrayIfNoTasksAfterLoadFromServer() throws IOException {
        changeHttpServer();

        assertTrue(newManager.getPrioritizedTasks().isEmpty());
    }

    @Test
    public void returnSortedTasksArrayAndLoadFromServer() throws IOException {
        LocalDateTime start1 = LocalDateTime.of(2003, 1, 28, 9, 30);
        LocalDateTime start2 = LocalDateTime.of(2003, 1, 29, 9, 30);
        Duration duration = Duration.ofMinutes(30);
        Task task1 = new Task(1, "task 1", State.NEW, "description task 1", null, null);
        Task task2 = new Task(2, "task 2", State.NEW, "description task 2", duration, start2);
        Task task3 = new Task(3, "task 3", State.NEW, "description task 3", duration, start1);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        changeHttpServer();

        List<Task> tasks = newManager.getPrioritizedTasks();

        assertEquals(3, tasks.size());
        assertEquals(task1, tasks.get(2));
        assertEquals(task2, tasks.get(1));
        assertEquals(task3, tasks.get(0));
    }

    @Test
    public void returnEmptySortedTasksArrayAfterFullDeleteFromManagerAndLoadFromServer() throws IOException {
        LocalDateTime start = LocalDateTime.of(2003, 1, 28, 9, 30);
        Duration duration = Duration.ofMinutes(30);
        manager.addTask(new Task(1, "task 1", State.NEW, "description task 1",  duration, start));
        int epicId = manager.addEpic(new Epic("epic", "description epic"));
        manager.addSubtask(new Subtask(3, "subtask 1", State.NEW, "description subtask 1",
                epicId, duration, start.plusDays(1)));
        manager.removeAllEpics();
        manager.removeAllTasks();

        changeHttpServer();

        assertTrue(newManager.getPrioritizedTasks().isEmpty());
    }
}
