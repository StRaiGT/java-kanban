package manager;

import model.Epic;
import model.State;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static model.TaskType.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTasksManager() {
        file = new File("src/resources/backup.csv");
    }

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        // Тестируем сохранение в файл
        File file = new File("src/resources/backup.csv");
        TaskManager fileBackedTasksManager = new FileBackedTasksManager(file);

        int taskId1 = fileBackedTasksManager.addTask(new Task(0, "task 1", State.NEW, "description task 1",
                Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 6, 30)));
        int taskId2 = fileBackedTasksManager.addTask(new Task(0, "task 2", State.NEW, "description task 2",
                Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 7, 30)));
        int taskId3 = fileBackedTasksManager.addTask(new Task(0, "task 1", State.NEW, "description task 1",
                Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 7, 00)));
        fileBackedTasksManager.getTask(taskId1);

        // Эта задача будет спускаться вниз по списку
        int taskId4 = fileBackedTasksManager.addTask(new Task("task 4", "description task 4"));

        int epicId1 = fileBackedTasksManager.addEpic(new Epic("epic 1", "description epic 1"));
        int epicId2 = fileBackedTasksManager.addEpic(new Epic("epic 2", "description epic 2"));
        int epicId3 = fileBackedTasksManager.addEpic(new Epic("epic 3", "description epic 3"));

        int subtaskId1 = fileBackedTasksManager.addSubtask(
                new Subtask(0, "subtask 1", State.NEW, "description subtask 1", epicId1,
                        Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 9, 30)));
        int subtaskId2 = fileBackedTasksManager.addSubtask(
                new Subtask(0, "subtask 2", State.NEW, "description subtask 2", epicId1,
                        Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 11, 30)));
        int subtaskId3 = fileBackedTasksManager.addSubtask(
                new Subtask(0, "subtask 3", State.NEW, "description subtask 3", epicId2,
                        Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 10, 30)));

        fileBackedTasksManager.getTask(taskId1);
        fileBackedTasksManager.getTask(taskId3);
        fileBackedTasksManager.getEpic(epicId2);
        fileBackedTasksManager.getEpic(epicId3);
        fileBackedTasksManager.getSubtask(subtaskId2);
        fileBackedTasksManager.getSubtask(subtaskId1);
        fileBackedTasksManager.updateSubtask(subtaskId3,
                new Subtask(subtaskId3, "subtask 3", State.DONE, "description subtask 3", epicId2));
        fileBackedTasksManager.getTask(taskId3);

        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println(fileBackedTasksManager.getPrioritizedTasks());

        // Тестируем загрузку из файла
        TaskManager managerFromFile = FileBackedTasksManager.loadFromFile(file);
        System.out.println(managerFromFile.getHistory());
        System.out.println(managerFromFile.getPrioritizedTasks());
    }

    // Добавить задачу
    @Override
    public int addTask(Task task) {
        int TaskId = super.addTask(task);
        save();
        return TaskId;
    }

    @Override
    public int addEpic(Epic epic) {
        int EpicId = super.addEpic(epic);
        save();
        return EpicId;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int subtaskId = super.addSubtask(subtask);
        save();
        return subtaskId;
    }

    // Вернуть список задач
    @Override
    public List<Task> getTasks() {
        List<Task> tasks = super.getTasks();
        save();
        return tasks;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> epics = super.getEpics();
        save();
        return epics;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> subtasks = super.getSubtasks();
        save();
        return subtasks;
    }

    // Получить задачу по id
    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    // Удалить задачу по id
    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    // Удалить все задачи
    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    // Обновить задачу
    @Override
    public void updateTask(int id, Task task) {
        super.updateTask(id, task);
        save();
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        super.updateEpic(id, epic);
        save();
    }

    @Override
    public void updateSubtask(int id, Subtask subtask) {
        super.updateSubtask(id, subtask);
        save();
    }

    // Сохранение в файл
    public void save() {
        try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))){
            bufferedWriter.write("id,type,name,status,description,duration,startTime,epicId&subtasksId\n");
            for (Task task : tasks.values()) {
                bufferedWriter.append(TaskUtils.toString(task));
                bufferedWriter.newLine();
            }
            for (Epic epic : epics.values()) {
                bufferedWriter.append(TaskUtils.toString(epic));
                bufferedWriter.newLine();
            }
            for (Subtask subtask : subtasks.values()) {
                bufferedWriter.append(TaskUtils.toString(subtask));
                bufferedWriter.newLine();
            }
            bufferedWriter.newLine();
            bufferedWriter.write(HistoryUtils.historyToString(inMemoryHistoryManager));
        } catch (IOException exp) {
            throw new ManagerSaveException("Ошибка при записи в файл.", exp);
        }
    }

    // Загрузка из файла
    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        int maxCounter = 1;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                line = bufferedReader.readLine();
                if (line.isEmpty()) {
                    break;
                }
                int id = TaskUtils.fromString(line).getId();
                if (TaskUtils.fromString(line).getTaskType().equals(TASK)) {
                    manager.tasks.put(id, TaskUtils.fromString(line));
                } else if (TaskUtils.fromString(line).getTaskType().equals(EPIC)) {
                    manager.epics.put(id, (Epic) TaskUtils.fromString(line));
                } else if (TaskUtils.fromString(line).getTaskType().equals(SUBTASK)) {
                    manager.subtasks.put(id, (Subtask) TaskUtils.fromString(line));
                }
                if (id > maxCounter) {
                    maxCounter = id;
                }
            }
            manager.counter = ++maxCounter;

            line = bufferedReader.readLine();
            for (int id : HistoryUtils.historyFromString(line)) {
                if (manager.tasks.containsKey(id)) {
                    manager.inMemoryHistoryManager.add(manager.getTask(id));
                } else if (manager.epics.containsKey(id)) {
                    manager.inMemoryHistoryManager.add(manager.getEpic(id));
                } else if (manager.subtasks.containsKey(id)) {
                    manager.inMemoryHistoryManager.add(manager.getSubtask(id));
                }
            }

            for (Task task : manager.getTasks()) {
                manager.prioritizedTasks.add(task);
            }
            for (Subtask subtask : manager.getSubtasks()) {
                manager.prioritizedTasks.add(subtask);
            }
        } catch (IOException exp) {
            throw new ManagerSaveException("Ошибка при чтении из файла.", exp);
        }
        return manager;
    }
}
