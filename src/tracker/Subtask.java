package tracker;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    int epicId;

    public Subtask(String heading, String description, int id, int epicId, Duration duration, LocalDateTime startTime) {
        super(heading, description, id, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicID() {
        return epicId;
    }

    @Override
    public LocalDateTime getEndTime() {
        return super.getEndTime(); // Используем реализацию из Task
    }

    @Override
    public String printTask() {
        return "Подзадача с идентификатором " + id + "\n" +
                "Название: " + heading + "\n" +
                "Описание: " + description + "\n" +
                "Статус: " + status + "\n" +
                "Принадлежит эпику с идентификатором " + epicId + "\n";
    }

    @Override
    public String toString() {
        return getId() + "," + Type.SUB + "," + getHeading() + "," + getStatus() + "," +
                getDescription() + "," + epicId + "," + duration.toMinutes() + "," + startTime;
    }

    public static Subtask fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 8) {
            throw new IllegalArgumentException("Неверный формат строки: " + value);
        }

        int id = Integer.parseInt(parts[0]);
        String heading = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        int epicId = Integer.parseInt(parts[5]);
        Duration duration = Duration.ofMinutes(Long.parseLong(parts[6]));
        LocalDateTime startTime = LocalDateTime.parse(parts[7]);

        Subtask subtask = new Subtask(heading, description, id, epicId, duration, startTime);
        subtask.setStatus(status);
        return subtask;
    }
}