package manager;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    public final int MAX_HISTORY = 10;
    private final List<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        history.add(task);
        if (history.size() > MAX_HISTORY) {
            history.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
