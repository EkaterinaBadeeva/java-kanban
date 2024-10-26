package model;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();
    private Instant endTime;

    public Epic(Integer id, String name, String description) {
        super(id, name, description);
        super.setStatus(Status.NEW);
        this.typeTasks = TypeTasks.EPIC;
    }

    public Epic(Integer id, String name, String description, Status status) {
        super(id, name, description, status);
        this.typeTasks = TypeTasks.EPIC;
    }

    public Epic(Integer id, String name, String description, Status status, ArrayList<Integer> subtaskIds) {
        super(id, name, description, status);
        this.subtaskIds = subtaskIds;
        this.typeTasks = TypeTasks.EPIC;
    }

    public Epic(Integer id, String name, String description, Status status, Instant startTime, Duration duration, Instant endTime) {
        super(id, name, description, status, startTime, duration);
        this.typeTasks = TypeTasks.EPIC;
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

    @Override
    public String toString() {

        return this.getId() + "," + typeTasks +
                "," + this.getName() + "," + this.getStatus() + "," +
                this.getDescription() + "," + dateToString(this.getStartTime()) + "," + this.getDurationOfMinutes() + ","
                + dateToString(this.getEndTime());
    }

    @Override
    public TypeTasks getTypeTasks() {
        return typeTasks;
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}

