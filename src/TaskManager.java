import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    public int counter = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    // Добавить задачу
    public int addTask(Task task) {
        tasks.put(counter, task);
        return counter++;
    }

    public int addEpic(Epic epic) {
        epics.put(counter, epic);
        return counter++;
    }

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
    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            result.add(task);
        }
        return result;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> result = new ArrayList<>();
        for (Epic epic : epics.values()) {
            result.add(epic);
        }
        return result;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            result.add(subtask);
        }
        return result;
    }

    // Получить задачу по id
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        return null;
    }

    // Удалить задачу по id
    public void removeTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            for (int subtaskId : epics.get(id).getSubtasksId()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    public void removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getEpicId()).removeSubtaskId(id);
            updateEpicStatus(subtasks.get(id).getEpicId());
            subtasks.remove(id);
        }
    }

    // Удалить все задачи
    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtasks() {
        for (int subtaskId : subtasks.keySet()) {
            epics.get(subtasks.get(subtaskId).getEpicId()).removeSubtaskId(subtaskId);
            updateEpicStatus(subtasks.get(subtaskId).getEpicId());
        }
        subtasks.clear();
    }

    // Обновить задачу
    public void updateTask(int id, Task task) {
        tasks.put(id, task);
    }

    public void updateEpic(int id, Epic epic) {
        epic.setSubtasksId(epics.get(id).getSubtasksId());
        epics.put(id, epic);
    }

    public void updateSubtask(int id, Subtask subtask) {
        subtasks.put(id, subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    // Получить список подзадач определенного эпика
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
    public void updateEpicStatus(int id) {
        if (epics.containsKey(id)) {
            if (epics.get(id).getSubtasksId() == null) {
                updateEpic(id, new Epic(epics.get(id),Task.STATE[0]));
            } else {
                boolean isNew = true;
                boolean isDone = true;
                for ( int subtaskId : epics.get(id).getSubtasksId()) {
                    if (!subtasks.get(subtaskId).getStatus().equals(Task.STATE[0])) {
                        isNew = false;
                    }
                    if (!subtasks.get(subtaskId).getStatus().equals(Task.STATE[2])) {
                        isDone = false;
                    }
                }
                if (isNew) {
                    updateEpic(id, new Epic(epics.get(id),Task.STATE[0]));
                } else if (isDone) {
                    updateEpic(id, new Epic(epics.get(id),Task.STATE[2]));
                } else {
                    updateEpic(id, new Epic(epics.get(id),Task.STATE[1]));
                }
            }
        }
    }
}
