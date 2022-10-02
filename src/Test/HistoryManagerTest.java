package Test;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import model.Task;
import model.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryManagerTest {
    private HistoryManager manager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    public void makeManager(){
        manager = new InMemoryHistoryManager();
        task1 = new Task(1, "task 1", State.NEW, "description task 1");
        task2 = new Task(2, "task 2", State.NEW, "description task 2");
        task3 = new Task(3, "task 3", State.NEW, "description task 3");
    }

    @Test
    public void shouldReturnEmptyHistoryIfHaveNoTasks(){
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void tasksShouldBeAddedInTheTailOfQuery(){
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        List<Task> tasks = manager.getHistory();

        assertEquals(3, tasks.size());
        assertEquals(task1, tasks.get(0));
        assertEquals(task2, tasks.get(1));
        assertEquals(task3, tasks.get(2));
    }

    @Test
    public void historyShouldBeEmptyAfterRemoveSingleTask(){
        manager.add(task1);
        manager.remove(task1.getId());
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void historyShouldContainOnlyOneTaskInTaleIfDuplicate(){
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.add(task2);
        manager.add(task2);
        List<Task> tasks = manager.getHistory();

        assertEquals(3, tasks.size());
        assertEquals(task1, tasks.get(0));
        assertEquals(task2, tasks.get(2));
    }

    @Test
    public void headShouldBeNextTaskInHistoryIfRemoveHead(){
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.remove(task1.getId());
        List<Task> tasks = manager.getHistory();

        assertEquals(2, tasks.size());
        assertEquals(task2, tasks.get(0));
        assertEquals(task3, tasks.get(1));
    }

    @Test
    public void tailShouldBePreviousTaskInHistoryIfRemoveTail(){
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.remove(task3.getId());
        List<Task> tasks = manager.getHistory();

        assertEquals(2, tasks.size());
        assertEquals(task1, tasks.get(0));
        assertEquals(task2, tasks.get(1));
    }

    @Test
    public void historyShouldContain2TasksAfterRemoveMiddle(){
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.remove(task2.getId());
        List<Task> tasks = manager.getHistory();

        assertEquals(2, tasks.size());
        assertEquals(task1, tasks.get(0));
        assertEquals(task3, tasks.get(1));
    }
}
