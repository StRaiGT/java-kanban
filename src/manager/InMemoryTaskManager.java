package manager;

import model.Epic;
import model.State;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager{
    protected int counter = 1;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>();
    HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    // Добавить задачу
    @Override
    public int addTask(Task task) {
        if (task == null || isIntersect(task)) {
            return -1;
        }
        Task newTask = new Task(counter, task);
        tasks.put(counter, newTask);
        prioritizedTasks.add(newTask);
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
        if (subtask == null || isIntersect(subtask)) {
            return -1;
        }
        int epicId = subtask.getEpicId();
        if (epics.containsKey(epicId)) {
            Subtask newSubtask = new Subtask(counter, subtask);
            subtasks.put(counter, newSubtask);
            epics.get(epicId).addSubtaskId(counter);

            updateEpicStatus(epicId);
            updateEpicDuration(epicId);
            prioritizedTasks.add(newSubtask);
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
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
        }
    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            for (int subtaskId : epics.get(id).getSubtasksId()) {
                inMemoryHistoryManager.remove(subtaskId);
                prioritizedTasks.remove(subtasks.get(subtaskId));
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
            prioritizedTasks.remove(subtasks.get(id));
            updateEpicDuration(subtasks.get(id).getEpicId());
            subtasks.remove(id);
        }
    }

    // Удалить все задачи
    @Override
    public void removeAllTasks() {
        for (Integer task : tasks.keySet()) {
            inMemoryHistoryManager.remove(task);
            prioritizedTasks.remove(tasks.get(task));
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
            prioritizedTasks.remove(subtasks.get(subtask));
        }
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (int subtaskId : subtasks.keySet()) {
            epics.get(subtasks.get(subtaskId).getEpicId()).removeSubtaskId(subtaskId);
            updateEpicStatus(subtasks.get(subtaskId).getEpicId());
            prioritizedTasks.remove(subtasks.get(subtaskId));
        }
        for (Integer subtask : subtasks.keySet()) {
            inMemoryHistoryManager.remove(subtask);
        }
        subtasks.clear();
    }

    // Обновить задачу
    @Override
    public void updateTask(int id, Task task) {
        if (task == null || isIntersect(task)) {
            return;
        }
        if (prioritizedTasks.contains(tasks.get(id))){
            prioritizedTasks.remove(tasks.get(id));
        }
        Task newTask = new Task(id, task);
        tasks.put(id, newTask);
        prioritizedTasks.add(newTask);
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
        if (subtask == null || isIntersect(subtask)) {
            return;
        }
        if (prioritizedTasks.contains(subtasks.get(id))){
            prioritizedTasks.remove(subtasks.get(id));
        }
        Subtask newSubtask = new Subtask(id, subtask);
        subtasks.put(id, newSubtask);
        updateEpicStatus(newSubtask.getEpicId());
        updateEpicDuration(subtasks.get(id).getEpicId());
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void updateEpicDuration(int id) {
        TreeSet<Subtask> set = new TreeSet<>(getEpicSubtasks(id).values());
        if (set.size() == 0) {
            updateEpic(id, new Epic(epics.get(id), null, null));
            return;
        }

        Subtask lastSubtask = set.last();
        Subtask firstSubtask = set.first();

        for (Subtask subtask : set){
            if (subtask.getStartTime() != null){
                firstSubtask = subtask;
                break;
            }
        }
        LocalDateTime start = firstSubtask.getStartTime();
        LocalDateTime startOfLastSubtask = lastSubtask.getStartTime();
        Duration duration = lastSubtask.getDuration();
        if (start != null && startOfLastSubtask != null && duration != null){
            updateEpic(id, new Epic(epics.get(id), Duration.between(start, startOfLastSubtask.plus(duration)), start));
        }
    }

    protected boolean isTasksIntersect(Task task1, Task task2){
        LocalDateTime firstTaskStartTime = task1.getStartTime();
        LocalDateTime firstTaskEndTime = task1.getEndTime();
        LocalDateTime secondTaskStartTime = task2.getStartTime();
        LocalDateTime secondTaskEndTime = task2.getEndTime();

        boolean isStartIntersect = (firstTaskStartTime.isEqual(secondTaskStartTime) ||
                firstTaskStartTime.isAfter(secondTaskStartTime)) && (firstTaskStartTime.isBefore(secondTaskEndTime) ||
                firstTaskStartTime.isEqual(secondTaskEndTime));
        boolean isEndIntersect = (firstTaskEndTime.isEqual(secondTaskStartTime) ||
                firstTaskEndTime.isAfter(secondTaskStartTime)) && (firstTaskEndTime.isBefore(secondTaskEndTime)
                || firstTaskEndTime.isEqual(secondTaskEndTime));
        return isStartIntersect || isEndIntersect;
    }

    protected boolean isIntersect(Task task) {
        if (task.getStartTime() == null || task.getDuration() == null) {
            return false;
        }
        for (Task anotherTask : prioritizedTasks) {
            if (anotherTask.getStartTime() == null || anotherTask.getDuration() == null) {
                return false;
            }
            if (isTasksIntersect(task, anotherTask)) {
                return true;
            }
        }
        return false;
    }
}
