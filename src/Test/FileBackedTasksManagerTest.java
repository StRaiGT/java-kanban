package Test;

import manager.FileBackedTasksManager;
import manager.TaskManager;
import model.Epic;
import model.State;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    static String path = "src/resources/testBackup.csv";
    private final Path fileToSaveData = Path.of(path);
    private final File fileToLoadFrom = new File(String.valueOf(fileToSaveData));

    public FileBackedTasksManagerTest() {
        super(new FileBackedTasksManager(new File(String.valueOf(Path.of(path)))));
    }

    @Test
    public void newManagerShouldBeEmptyAfterLoadingFromEmptyFile() {
        manager.save();
        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(fileToLoadFrom);
        assertEquals(manager.getEpics(), newManager.getEpics());
        assertEquals(manager.getTasks(), newManager.getTasks());
        assertEquals(manager.getSubtasks(), newManager.getSubtasks());
        assertEquals(manager.getHistory(), newManager.getHistory());
        assertTrue(newManager.getEpics().isEmpty());
        assertTrue(newManager.getTasks().isEmpty());
        assertTrue(newManager.getSubtasks().isEmpty());
        assertTrue(newManager.getHistory().isEmpty());
    }

    @Test
    public void loadEpicWithoutSubtasksFromFileToManagerWithoutSubtasks(){
        int epicId1 = manager.addEpic(new Epic("epic 1", "description epic 1"));
        manager.save();

        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(fileToLoadFrom);
        assertEquals(manager.getEpics(), newManager.getEpics());
        assertEquals(manager.getTasks(), newManager.getTasks());
        assertEquals(manager.getSubtasks(), newManager.getSubtasks());
        assertEquals(manager.getHistory(), newManager.getHistory());
        assertTrue(newManager.getEpics().contains(manager.getEpic(epicId1)));
        assertTrue(newManager.getTasks().isEmpty());
        assertEquals(1, newManager.getEpics().size());
        assertTrue(newManager.getSubtasks().isEmpty());
        assertEquals(manager.getEpic(epicId1).getSubtasksId(), newManager.getEpic(epicId1).getSubtasksId());
    }

    @Test
    public void historyShouldBeEmptyAfterLoadFromFileWithEmptyHistory() {
        manager.addTask(new Task("task 1", "description task 1"));
        manager.addTask(new Task("task 2", "description task 2"));
        manager.addTask(new Task("task 3", "description task 3"));
        manager.save();

        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(fileToLoadFrom);
        assertTrue(newManager.getHistory().isEmpty());
    }
}
