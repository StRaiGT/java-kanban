package manager;

import model.*;

import java.util.ArrayList;
import java.util.List;

public abstract class TaskUtils {
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

    // Создание задачи из подстроки
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
}
