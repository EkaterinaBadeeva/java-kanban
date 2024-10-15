package model;

import java.time.Duration;
import java.time.Instant;

public class Subtask extends Task {
    private int epicId;
    private TypeTasks typeTasks = TypeTasks.SUBTASK;

    public Subtask(Integer id, String name, String description, Status status, Instant startTime, Duration duration, int epicId) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {

        return this.getId() + "," + typeTasks +
                "," + this.getName() + "," + this.getStatus() + "," +
                this.getDescription() + "," + dateToString(this.getStartTime()) + "," + this.getDurationOfMinutes() + ","
                + dateToString(this.getEndTime()) + "," + epicId;
    }

    @Override
    public TypeTasks getTypeTasks() {
        return typeTasks;
    }
}
