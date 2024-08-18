import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    public InMemoryHistoryManager() {

    }

    private List<Task> history = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return new ArrayList<Task>(history);
    }

    @Override
    public void addHistory(Task object) {
        if (object == null) {
            return;
        }
        if (history.size() < 10) {
            history.addLast(object);
        } else {
            history.removeFirst();
            history.addLast(object);
        }
    }
}
