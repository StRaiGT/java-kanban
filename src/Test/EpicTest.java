package Test;

import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.State;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private static Epic epic;
    private static TaskManager manager = Managers.getDefault();

    @BeforeEach
    public void addNewEpic(){
        manager = Managers.getDefault();
        epic = new Epic("epic 1", "description epic 1");
        int epicId1 = manager.addEpic(epic);
        int subtaskId1 = manager.addSubtask(
                new Subtask("subtask 1", "description subtask 1", epicId1));
        int subtaskId2 = manager.addSubtask(
                new Subtask("subtask 2", "description subtask 2", epicId1));
        int subtaskId3 = manager.addSubtask(
                new Subtask("subtask 3", "description subtask 3", epicId1));
    }

    @Test
    public void epicStateShouldBeNewWithEmptySubtasksList() {
        manager.removeAllSubtasks();

        assertEquals(State.NEW, manager.getEpics().get(0).getStatus());
    }

    @Test
    public void epicStateShouldBeNewWithSubtasksAllNewState() {
        assertEquals(State.NEW, manager.getEpics().get(0).getStatus());
    }

    @Test
    public void epicStateShouldBeDoneWithSubtasksAllDoneState() {
        for(Subtask subtask : manager.getSubtasks()) {
            manager.updateSubtask(subtask.getId(), new Subtask(subtask, State.DONE));
        }

        assertEquals(State.DONE, manager.getEpics().get(0).getStatus());
    }

    @Test
    public void epicStateShouldBeInProgressWithSubtasksNewAndDoneState() {
        manager.updateSubtask(manager.getSubtasks().get(0).getId(),
                new Subtask(manager.getSubtasks().get(0), State.DONE));

        assertEquals(State.IN_PROGRESS, manager.getEpics().get(0).getStatus());
    }

    @Test
    public void epicStateShouldBeInProgressWithSubtasksAllInProgressState() {
        for(Subtask subtask : manager.getSubtasks()) {
            manager.updateSubtask(subtask.getId(), new Subtask(subtask, State.IN_PROGRESS));
        }

        assertEquals(State.IN_PROGRESS, manager.getEpics().get(0).getStatus());
    }

     @Test
    public void startTimeAndEndTimeShouldBeNullIfSubtasksWithoutStartTimeAndDuration() {
         manager.addSubtask(new Subtask(0, "subtask 1", State.NEW, "description subtask 1",
                epic.getId(), null, null));

        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertNull(epic.getDuration());
    }

    @Test
    public void epicShouldHaveStartTimeAndDurationIfSubtasksHaveIt() {
        manager.removeAllSubtasks();
        manager.addSubtask(new Subtask(0, "subtask 1", State.NEW, "description subtask 1",
                1, Duration.ofHours(2), LocalDateTime.of(2003, 1, 28, 9, 30)));

        assertEquals(manager.getEpic(1).getStartTime(),
                LocalDateTime.of(2003, 1, 28, 9, 30));
        assertEquals(manager.getEpic(1).getEndTime(),
                LocalDateTime.of(2003, 1, 28, 9, 30).plus(Duration.ofHours(2)));
        assertEquals(manager.getEpic(1).getDuration(), Duration.ofHours(2));
    }

    @Test
    public void epicShouldHaveStartTimeAndDurationIfNotAllSubtasksHaveIt() {
        manager.removeAllSubtasks();
        manager.addSubtask(new Subtask(0, "subtask 1", State.NEW, "description subtask 1",
                1, Duration.ofHours(2), LocalDateTime.of(2003, 1, 28, 9, 30)));
        manager.addSubtask(new Subtask(0, "subtask 2", State.NEW, "description subtask 2",
                1, null, null));

        assertEquals(manager.getEpic(1).getStartTime(),
                LocalDateTime.of(2003, 1, 28, 9, 30));
        assertEquals(manager.getEpic(1).getEndTime(),
                LocalDateTime.of(2003, 1, 28, 9, 30).plus(Duration.ofHours(2)));
        assertEquals(manager.getEpic(1).getDuration(), Duration.ofHours(2));
    }

    @Test
    public void epicDurationCanBeLongerThenSumOfDurationOfSubtasks() {
        manager.removeAllSubtasks();
        manager.addSubtask(new Subtask(0, "subtask 1", State.NEW, "description subtask 1",
                1, Duration.ofHours(2), LocalDateTime.of(2003, 1, 28, 9, 30)));
        manager.addSubtask(new Subtask(0, "subtask 1", State.IN_PROGRESS, "description subtask 1",
                1, Duration.ofHours(2), LocalDateTime.of(2003, 1, 28, 13, 30)));

        assertEquals(manager.getEpic(1).getStartTime(),
                LocalDateTime.of(2003, 1, 28, 9, 30));
        assertEquals(manager.getEpic(1).getEndTime(),
                LocalDateTime.of(2003, 1, 28, 9, 30).plus(Duration.ofHours(6)));
        assertEquals(manager.getEpic(1).getDuration(), Duration.ofHours(6));
    }

    @Test
    public void shouldNotAddSubtasksWhichHasTimeIntersection(){
        manager.removeAllSubtasks();
        manager.addSubtask(new Subtask(0, "subtask 1", State.NEW, "description subtask 1",
                1, Duration.ofHours(2), LocalDateTime.of(2003, 1, 28, 9, 30)));
        manager.addSubtask(new Subtask(0, "subtask 1", State.IN_PROGRESS, "description subtask 1",
                1, Duration.ofHours(2), LocalDateTime.of(2003, 1, 28, 10, 30)));

        assertEquals(1, epic.getSubtasksId().size());
        assertEquals(manager.getEpic(1).getStartTime(),
                LocalDateTime.of(2003, 1, 28, 9, 30));
        assertEquals(manager.getEpic(1).getEndTime(),
                LocalDateTime.of(2003, 1, 28, 9, 30).plus(Duration.ofHours(2)));
        assertEquals(manager.getEpic(1).getDuration(), Duration.ofHours(2));
    }
}