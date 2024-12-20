package tracker;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    protected String heading;
    protected String description;
    protected Status status;
    protected int id;
    protected Duration duration; // Продолжительность задачи
    protected LocalDateTime startTime; // Дата начала задачи

    public Task(String heading, String description, int id, Duration duration, LocalDateTime startTime) {
        this.heading = heading;
        this.description = description;
        this.id = id;
        this.status = Status.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return (startTime != null && duration != null) ? startTime.plus(duration) : null;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String printTask() {
        return "Обычная задача с идентификатором " + id + "\n" +
                "Название: " + heading + "\n" +
                "Описание: " + description + "\n" +
                "Статус: " + status + "\n" +
                "Продолжительность: " + (duration != null ? duration.toMinutes() + " минут" : "не указана") + "\n" +
                "Дата начала: " + (startTime != null ? startTime.toString() : "не указана") + "\n" +
                "Дата окончания: " + getEndTime();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeading() {
        return heading;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return getId() + "," + Type.TASK + "," + getHeading() + "," + getStatus() + "," + getDescription() + "," +
                (duration != null ? duration.toMinutes() : "0") + "," +
                (startTime != null ? startTime.toString() : "");
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 6) { // Обновлено для учета новых полей
            throw new IllegalArgumentException("Неверный формат строки: " + value);
        }

        int id = Integer.parseInt(parts[0]);
        String heading = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        Duration duration = Duration.ofMinutes(Long.parseLong(parts[5]));
        LocalDateTime startTime = parts.length > 6 && !parts[6].isEmpty() ? LocalDateTime.parse(parts[6]) : null;

        Task task = new Task(heading, description, id, duration, startTime);
        task.setStatus(status);
        return task;
    }

    public boolean isOverlapping(Task other) {
        if (this.startTime == null || other.startTime == null) {
            return false;
        }

        LocalDateTime thisEndTime = this.getEndTime();
        LocalDateTime otherEndTime = other.getEndTime();

        return this.startTime.isBefore(otherEndTime) && thisEndTime.isAfter(other.startTime);
    }
}