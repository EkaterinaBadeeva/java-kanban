import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(Integer id, String name, String description) {
        super(id, name, description);
        super.setStatus(Status.NEW);
    }

    public Epic(Integer id, String name, String description, Status status, ArrayList<Integer> subtaskIds) {
        super(id, name, description, status);
        this.subtaskIds = subtaskIds;
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
        return new ArrayList<Integer>(subtaskIds);
    }

    public boolean isEmptySubtaskIds() {
        return subtaskIds.isEmpty();
    }

    public void deleteAllSubtaskIds() {
        if (!isEmptySubtaskIds()) {
            subtaskIds.clear();
        }
    }
}

