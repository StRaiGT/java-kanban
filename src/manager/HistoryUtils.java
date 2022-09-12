package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public abstract class HistoryUtils {
    // Сохранение истории в строку
    public static String historyToString(HistoryManager historyManager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : historyManager.getHistory()) {
            sb.append(task.getId());
            sb.append(",");
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
}
