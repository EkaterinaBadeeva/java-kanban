package manager;

import model.Task;

import java.util.List;
import java.util.Set;

public interface HistoryManager {
    List<Task> getHistory();

    void addHistory(Task object);

    void removeHistory(int id);

    void removeHistory (Set<Integer> ids);
}
