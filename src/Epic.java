import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(Integer id, String name, String description) {
        super(id, name, description);
        this.subtaskIds = subtaskIds;
        super.status = Status.NEW;
    }

    public void addSubtaskId(int subtaskId) {
        if (!subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
        } else {
            System.out.println("Задача с таким id уже есть в этом эпике");
        }
    }

    public void deleteSubtaskId(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isEmptySubtaskIds () {
        return subtaskIds.isEmpty();
    }
}

