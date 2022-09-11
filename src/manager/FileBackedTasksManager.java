package manager;

import model.Epic;
import model.State;
import model.TaskType;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static model.TaskType.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        // Тестируем сохранение в файл
        File file = new File("src/resources/backup.csv");
        TaskManager fileBackedTasksManager = new FileBackedTasksManager(file);

        int taskId1 = fileBackedTasksManager.addTask(new Task("task 1", "description task 1"));
        int taskId2 = fileBackedTasksManager.addTask(new Task("task 2", "description task 2"));
        int taskId3 = fileBackedTasksManager.addTask(new Task("task 3", "description task 3"));
        fileBackedTasksManager.getTask(taskId1);

        int epicId1 = fileBackedTasksManager.addEpic(new Epic("epic 1", "description epic 1"));
        int epicId2 = fileBackedTasksManager.addEpic(new Epic("epic 2", "description epic 2"));
        int epicId3 = fileBackedTasksManager.addEpic(new Epic("epic 3", "description epic 3"));

        int subtaskId1 = fileBackedTasksManager.addSubtask(
                new Subtask("subtask 1", "description subtask 1", epicId1));
        int subtaskId2 = fileBackedTasksManager.addSubtask(
                new Subtask("subtask 2", "description subtask 2", epicId1));
        int subtaskId3 = fileBackedTasksManager.addSubtask(
                new Subtask("subtask 3", "description subtask 3", epicId2));

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

        // Тестируем загрузку из файла
        TaskManager managerFromFile = FileBackedTasksManager.loadFromFile(file);
        System.out.println(managerFromFile.getHistory());
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

    // Сохранение задачи в строку
    public static String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s",
                task.getId(), task.getTaskType(), task.getName(), task.getStatus(), task.getDescription());
    }

    public static String toString(Epic epic) {
        return String.format("%d,%s,%s,%s,%s,%s", epic.getId(), epic.getTaskType(), epic.getName(),
                epic.getStatus(), epic.getDescription(), epic.getSubtasksId());
    }

    public static String toString(Subtask subtask) {
        return String.format("%d,%s,%s,%s,%s,%d", subtask.getId(), subtask.getTaskType(), subtask.getName(),
                subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
    }

    // Создаем задачу из подстроки
    public static Task fromString(String text) {
        String[] splitText = text.split(",");

        int id = Integer.parseInt(splitText[0]);
        TaskType taskType = TaskType.valueOf(splitText[1]);
        String name = splitText[2];
        State status = State.valueOf(splitText[3]);
        String description = splitText[4];

        switch (taskType) {
            case TASK:
                return new Task(id, name, status, description);
            case EPIC:
                List<Integer> subtasksId = new ArrayList<>();
                if (splitText.length > 5) {
                    for (int i = 5; i < splitText.length; i++) {
                        String s = splitText[i].trim().replaceAll("[\\[\\]]", "");
                        if (!s.isEmpty()) {
                            subtasksId.add(Integer.parseInt(s));
                        }
                    }
                }
                return new Epic(id, name, status, description, subtasksId);
            case SUBTASK:
                int epicId = Integer.parseInt(splitText[5]);
                return new Subtask(id, name, status, description, epicId);
            default:
                return null;
        }
    }

    // Сохранение истории в строку
    public static String historyToString(HistoryManager historyManager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : historyManager.getHistory()) {
            sb.append(task.getId() + ",");
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    // Чтение истории из строки
    public static List<Integer> historyFromString(String text) {
        String[] splitText = text.split(",");
        List<Integer> list = new ArrayList<>();
        for (String s : splitText) {
            list.add(Integer.parseInt(s));
        }
        return list;
    }

    // Сохранение в файл
    public void save() {
        try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))){
            bufferedWriter.write("id,type,name,status,description,epicId&subtasksId\n");
            for (Task task : tasks.values()) {
                bufferedWriter.append(toString(task));
                bufferedWriter.newLine();
            }
            for (Epic epic : epics.values()) {
                bufferedWriter.append(toString(epic));
                bufferedWriter.newLine();
            }
            for (Subtask subtask : subtasks.values()) {
                bufferedWriter.append(toString(subtask));
                bufferedWriter.newLine();
            }
            bufferedWriter.newLine();
            bufferedWriter.write(historyToString(inMemoryHistoryManager));
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
                int id = fromString(line).getId();
                if (fromString(line).getTaskType().equals(TASK)) {
                    manager.tasks.put(id, fromString(line));
                } else if (fromString(line).getTaskType().equals(EPIC)) {
                    manager.epics.put(id, (Epic) fromString(line));
                } else if (fromString(line).getTaskType().equals(SUBTASK)) {
                    manager.subtasks.put(id, (Subtask) fromString(line));
                }
                if (id > maxCounter) {
                    maxCounter = id;
                }
            }
            manager.counter = ++maxCounter;

            line = bufferedReader.readLine();
            for (int id : historyFromString(line)) {
                if (manager.tasks.containsKey(id)) {
                    manager.inMemoryHistoryManager.add(manager.getTask(id));
                } else if (manager.epics.containsKey(id)) {
                    manager.inMemoryHistoryManager.add(manager.getEpic(id));
                } else if (manager.subtasks.containsKey(id)) {
                    manager.inMemoryHistoryManager.add(manager.getSubtask(id));
                }
            }
        } catch (IOException exp) {
            throw new ManagerSaveException("Ошибка при чтении из файла.", exp);
        }
        return manager;
    }
}
