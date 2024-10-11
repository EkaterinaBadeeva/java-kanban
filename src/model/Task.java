package model;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Status status;
    private Integer id;
    private TypeTasks typeTasks = TypeTasks.TASK;
    private Duration duration;
    private Instant startTime;
    private Instant endTime;

    // Конструктор для тестов в Main
    public Task(Integer id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = Instant.now();
        this.duration = Duration.ofMinutes(0);
    }

    public Task(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startTime = Instant.now();
        this.duration = Duration.ofMinutes(0);
    }

    public Task(Integer id, String name, String description, Status status, Instant startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = getEndTime();
    }

    public Task(Integer id, String name, String description, Status status, Instant startTime, Duration duration, Instant endTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = getEndTime();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + "," + typeTasks + "," + name + ","
                + status + "," + description + "," + dateToString(startTime) + "," + duration.toMinutes() + ","
                + dateToString(endTime);
    }

    protected String dateToString(Instant time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        if (time != null) {
            LocalDateTime timeToString = LocalDateTime.ofInstant(time, ZoneOffset.UTC);
            return timeToString.format(formatter);
        }
        return LocalDateTime.of(1, 1, 1, 0, 0, 0).format(formatter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public TypeTasks getTypeTasks() {
        return typeTasks;
    }

    public Instant getEndTime() {
        if (startTime != null)
            return startTime.plus(duration);
        return null;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
