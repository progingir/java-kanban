package tracker;

import java.time.Instant;

public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected Status status;
    protected Type type;
    protected Instant startTime;
    protected long duration;

    public Task(String title, String description, Integer id, Status status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.type = Type.TASK;
    }

    public Task(int id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = Type.TASK;
    }

    public Task(int id, String title, String description, long duration, Instant startTime, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.status = status;
        this.type = Type.TASK;
    }

    public Task() {}

    public Instant getEndTime() {
        long seconds = 60L;
        return startTime.plusSeconds(duration * seconds);
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Type getType() {
        return type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + getId() +
                ", status='" + status + ", start time=" + startTime.toEpochMilli() +
                ", duration=" + duration + ", end time=" + getEndTime().toEpochMilli() + '\'' + '}';
    }

    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                id, type, title, status, description, startTime, duration, "");
    }

}
