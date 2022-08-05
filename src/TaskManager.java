import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    // Добавить задачу
    int addTask(Task task);
    int addEpic(Epic epic);
    int addSubtask(Subtask subtask);

    // Вернуть список задач
    ArrayList<Task> getTasks();
    ArrayList<Epic> getEpics();
    ArrayList<Subtask> getSubtasks();

    // Получить задачу по id
    Task getTask(int id);
    Epic getEpic(int id);
    Subtask getSubtask(int id);

    // Удалить задачу по id
    void removeTask(int id);
    void removeEpic(int id);
    void removeSubtask(int id);

    // Удалить все задачи
    void removeAllTasks();
    void removeAllEpics();
    void removeAllSubtasks();

    // Обновить задачу
    void updateTask(int id, Task task);
    void updateEpic(int id, Epic epic);
    void updateSubtask(int id, Subtask subtask);

    // Получить список подзадач определенного эпика
    HashMap<Integer, Subtask> getEpicSubtasks(int id);

    // Обновление статуса эпика
    void updateEpicStatus(int id);

    // Вернуть историю просмотра задач
    List<Task> getHistory();
}

