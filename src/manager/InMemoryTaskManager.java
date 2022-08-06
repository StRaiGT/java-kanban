package manager;

import model.Epic;
import model.State;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager{
    public int counter = 1;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    // Добавить задачу
    @Override
    public int addTask(Task task) {
        tasks.put(counter, task);
        return counter++;
    }

    @Override
    public int addEpic(Epic epic) {
        epics.put(counter, epic);
        return counter++;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (epics.containsKey(epicId)) {
            subtasks.put(counter, subtask);
            epics.get(epicId).addSubtaskId(counter);
            updateEpicStatus(epicId);
            return counter++;
        }
        return -1;
    }

    // Вернуть список задач
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Получить задачу по id
    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            inMemoryHistoryManager.add(tasks.get(id));
            return tasks.get(id);
        }
        return null;
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            inMemoryHistoryManager.add(epics.get(id));
            return epics.get(id);
        }
        return null;
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            inMemoryHistoryManager.add(subtasks.get(id));
            return subtasks.get(id);
        }
        return null;
    }

    // Удалить задачу по id
    @Override
    public void removeTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            for (int subtaskId : epics.get(id).getSubtasksId()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    @Override
    public void removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getEpicId()).removeSubtaskId(id);
            updateEpicStatus(subtasks.get(id).getEpicId());
            subtasks.remove(id);
        }
    }

    // Удалить все задачи
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (int subtaskId : subtasks.keySet()) {
            epics.get(subtasks.get(subtaskId).getEpicId()).removeSubtaskId(subtaskId);
            updateEpicStatus(subtasks.get(subtaskId).getEpicId());
        }
        subtasks.clear();
    }

    // Обновить задачу
    @Override
    public void updateTask(int id, Task task) {
        tasks.put(id, task);
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        epic.setSubtasksId(epics.get(id).getSubtasksId());
        epics.put(id, epic);
    }

    @Override
    public void updateSubtask(int id, Subtask subtask) {
        subtasks.put(id, subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    // Получить список подзадач определенного эпика
    @Override
    public HashMap<Integer, Subtask> getEpicSubtasks(int id) {
        if (epics.containsKey(id)) {
            HashMap<Integer, Subtask> epicSubtasks = new HashMap<>();
            for (int subtaskId : epics.get(id).getSubtasksId()) {
                epicSubtasks.put(subtaskId, subtasks.get(subtaskId));
            }
            return epicSubtasks;
        }
        return null;
    }

    // Обновление статуса эпика
    @Override
    public void updateEpicStatus(int id) {
        if (epics.containsKey(id)) {
            if (epics.get(id).getSubtasksId() == null) {
                updateEpic(id, new Epic(epics.get(id), State.NEW));
            } else {
                boolean isNew = true;
                boolean isDone = true;
                for (int subtaskId : epics.get(id).getSubtasksId()) {
                    if (!subtasks.get(subtaskId).getStatus().equals(State.NEW)) {
                        isNew = false;
                    }
                    if (!subtasks.get(subtaskId).getStatus().equals(State.DONE)) {
                        isDone = false;
                    }
                }
                if (isNew) {
                    updateEpic(id, new Epic(epics.get(id),State.NEW));
                } else if (isDone) {
                    updateEpic(id, new Epic(epics.get(id),State.DONE));
                } else {
                    updateEpic(id, new Epic(epics.get(id),State.IN_PROGRESS));
                }
            }
        }
    }

    // Вернуть историю просмотра задач
    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }
}
