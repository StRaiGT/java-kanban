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
    protected int counter = 1;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    // Добавить задачу
    @Override
    public int addTask(Task task) {
        if (task == null) {
            return -1;
        }
        tasks.put(counter, new Task(counter, task));
        return counter++;
    }

    @Override
    public int addEpic(Epic epic) {
        if (epic == null) {
            return -1;
        }
        epics.put(counter, new Epic(counter, epic));
        return counter++;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (subtask == null) {
            return -1;
        }
        int epicId = subtask.getEpicId();
        if (epics.containsKey(epicId)) {
            subtasks.put(counter, new Subtask(counter, subtask));
            epics.get(epicId).addSubtaskId(counter);
            updateEpicStatus(epicId);
            return counter++;
        }
        return -1;
    }

    // Вернуть список задач
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
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
            inMemoryHistoryManager.remove(id);
            tasks.remove(id);
        }
    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            for (int subtaskId : epics.get(id).getSubtasksId()) {
                inMemoryHistoryManager.remove(subtaskId);
                subtasks.remove(subtaskId);
            }
            inMemoryHistoryManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public void removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getEpicId()).removeSubtaskId(id);
            updateEpicStatus(subtasks.get(id).getEpicId());
            inMemoryHistoryManager.remove(id);
            subtasks.remove(id);
        }
    }

    // Удалить все задачи
    @Override
    public void removeAllTasks() {
        for (Integer task : tasks.keySet()) {
            inMemoryHistoryManager.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Integer epic : epics.keySet()) {
            inMemoryHistoryManager.remove(epic);
        }
        epics.clear();
        for (Integer subtask : subtasks.keySet()) {
            inMemoryHistoryManager.remove(subtask);
        }
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (int subtaskId : subtasks.keySet()) {
            epics.get(subtasks.get(subtaskId).getEpicId()).removeSubtaskId(subtaskId);
            updateEpicStatus(subtasks.get(subtaskId).getEpicId());
        }
        for (Integer subtask : subtasks.keySet()) {
            inMemoryHistoryManager.remove(subtask);
        }
        subtasks.clear();
    }

    // Обновить задачу
    @Override
    public void updateTask(int id, Task task) {
        if (task == null) {
            return;
        }
        tasks.put(id, new Task(id, task));
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        if (epic == null) {
            return;
        }
        epic.setSubtasksId(epics.get(id).getSubtasksId());
        epics.put(id, new Epic(id, epic));
    }

    @Override
    public void updateSubtask(int id, Subtask subtask) {
        if (subtask == null) {
            return;
        }
        subtasks.put(id, new Subtask(id, subtask));
        updateEpicStatus(subtask.getEpicId());
    }

    // Получить список подзадач определенного эпика
    @Override
    public Map<Integer, Subtask> getEpicSubtasks(int id) {
        if (epics.containsKey(id)) {
            Map<Integer, Subtask> epicSubtasks = new HashMap<>();
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
