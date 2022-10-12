package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTasksManager;
import manager.ManagerSaveException;
import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskType;

import java.util.ArrayList;
import java.util.Map;

import static model.TaskType.*;

public class HttpTaskManager extends FileBackedTasksManager {
    public final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager(String url) {
        super();
        gson = Managers.getGson();
        this.kvTaskClient = new KVTaskClient(url);
        loadManagerStatus();
    }

    private void loadManagerStatus() {
        try {
            String jsonTasks = kvTaskClient.load("tasks/task");
            Map<Integer, Task> tasksFromJson = gson.fromJson(jsonTasks, new TypeToken<Map<Integer, Task>>() {
            }.getType());
            if (jsonTasks != null) {
                for (Task task : tasksFromJson.values()) {
                    tasks.put(task.getId(), task);
                    prioritizedTasks.add(task);
                }
            }

            String jsonEpics = kvTaskClient.load("tasks/epic");
            Map<Integer, Epic> epicsFromJson = gson.fromJson(jsonEpics, new TypeToken<Map<Integer, Epic>>() {
            }.getType());
            if (jsonEpics != null) {
                for (Epic epic : epicsFromJson.values()) {
                    epics.put(epic.getId(), epic);
                }
            }

            String jsonSubtasks = kvTaskClient.load("tasks/subtask");
            Map<Integer, Subtask> subtasksFromJson = gson.fromJson(jsonSubtasks, new TypeToken<Map<Integer, Subtask>>() {
            }.getType());
            if (subtasksFromJson != null) {
                for (Subtask subtask : subtasksFromJson.values()) {
                    subtasks.put(subtask.getId(), subtask);
                    prioritizedTasks.add(subtask);
                }
            }

            String jsonHistory = kvTaskClient.load("tasks/history");
            if (jsonHistory != null) {
                ArrayList<Task> history = gson.fromJson(jsonHistory, new TypeToken<ArrayList<Task>>() {
                }.getType());
                for (int i = history.size() - 1; i >= 0; i--) {
                    int id = history.get(i).getId();
                    TaskType taskType = history.get(i).getTaskType();
                    if (taskType == TASK) {
                        getTask(id);
                    } else if (taskType == EPIC) {
                        getEpic(id);
                    } else if (taskType == SUBTASK){
                        getSubtask(id);
                    }
                }
            }
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка в загрузке");
        }
    }

    @Override
    public void save() {
        try {
            kvTaskClient.put("tasks/task", gson.toJson(tasks));
            kvTaskClient.put("tasks/epic", gson.toJson(epics));
            kvTaskClient.put("tasks/subtask", gson.toJson(subtasks));
            kvTaskClient.put("tasks/history", gson.toJson(getHistory()));
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка в сохранении");
        }
    }
}