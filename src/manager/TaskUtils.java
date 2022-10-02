package manager;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class TaskUtils {
    // Сохранение задачи в строку
    public static String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s,%s", task.getId(), task.getTaskType(), task.getName(),
                task.getStatus(), task.getDescription(), task.getDuration(), task.getStartTime());
    }

    public static String toString(Epic epic) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s", epic.getId(), epic.getTaskType(), epic.getName(),
                epic.getStatus(), epic.getDescription(), epic.getDuration(), epic.getStartTime(), epic.getSubtasksId());
    }

    public static String toString(Subtask subtask) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%d", subtask.getId(), subtask.getTaskType(), subtask.getName(),
                subtask.getStatus(), subtask.getDescription(), subtask.getDuration(), subtask.getStartTime(),
                subtask.getEpicId());
    }

    // Создание задачи из подстроки
    public static Task fromString(String text) {
        String[] splitText = text.split(",");

        int id = Integer.parseInt(splitText[0]);
        TaskType taskType = TaskType.valueOf(splitText[1]);
        String name = splitText[2];
        State status = State.valueOf(splitText[3]);
        String description = splitText[4];
        Duration duration;
        if (splitText[5].equals("null")){
            duration = null;
        } else {
            duration = Duration.parse(splitText[5]);
        }
        LocalDateTime startTime;
        if (splitText[6].equals("null")){
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(splitText[6]);
        }

        switch (taskType) {
            case TASK:
                return new Task(id, name, status, description, duration, startTime);
            case EPIC:
                List<Integer> subtasksId = new ArrayList<>();
                if (splitText.length > 7) {
                    for (int i = 7; i < splitText.length; i++) {
                        String s = splitText[i].trim().replaceAll("[\\[\\]]", "");
                        if (!s.isEmpty()) {
                            subtasksId.add(Integer.parseInt(s));
                        }
                    }
                }
                return new Epic(id, name, status, description, subtasksId, duration, startTime);
            case SUBTASK:
                int epicId = Integer.parseInt(splitText[7]);
                return new Subtask(id, name, status, description, epicId, duration, startTime);
            default:
                return null;
        }
    }
}
