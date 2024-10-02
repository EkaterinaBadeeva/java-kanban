package model;

public class Subtask extends Task {
    private int epicId;
    private TypeTasks typeTasks = TypeTasks.SUBTASK;

    public Subtask(Integer id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, int epicId) {
        super(id, name, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {

        return this.getId() + "," + typeTasks +
                "," + this.getName() + "," + this.getStatus() + "," +
                this.getDescription() + "," + epicId;
    }

    @Override
    public TypeTasks getTypeTasks() {
        return typeTasks;
    }
}
