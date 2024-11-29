package tracker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<Subtask> subtasks;

    public EpicTask(String heading, String description, int id) {
        super(heading, description, id, Duration.ZERO, null); // Устанавливаем нулевую продолжительность и null для startTime
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        calculateEpicDetails();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    private void calculateEpicDetails() {
        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStartTime = null;
        LocalDateTime latestEndTime = null;

        for (Subtask subtask : subtasks) {
            totalDuration = totalDuration.plus(subtask.getDuration());
            if (earliestStartTime == null || (subtask.getStartTime() != null && subtask.getStartTime().isBefore(earliestStartTime))) {
                earliestStartTime = subtask.getStartTime();
            }
            if (latestEndTime == null || (subtask.getEndTime() != null && subtask.getEndTime().isAfter(latestEndTime))) {
                latestEndTime = subtask.getEndTime();
            }
        }

        this.duration = totalDuration;
        this.startTime = earliestStartTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return super.getEndTime(); // Используем реализацию из Task
    }

    @Override
    public String printTask() {
        return "Глобальная задача с идентификатором " + id + "\n" +
                "Название: " + heading + "\n" +
                "Описание: " + description + "\n" +
                "Статус: " + status + "\n" +
                "Продолжительность: " + duration.toMinutes() + " минут\n" +
                "Время начала: " + (startTime != null ? startTime : "Не задано");
    }

    @Override
    public String toString() {
        return getId() + "," + Type.EPIC + "," + getHeading() + "," + getStatus() + "," +
                getDescription() + "," + duration.toMinutes() + "," + (startTime != null ? startTime : "");
    }

    public static EpicTask fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 7) { // Минимальное количество частей для корректного разбора
            throw new IllegalArgumentException("Неверный формат строки: " + value);
        }

        int id = Integer.parseInt(parts[0]);
        String heading = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        Duration duration = Duration.ofMinutes(Long.parseLong(parts[5]));
        LocalDateTime startTime = parts[6].isEmpty() ? null : LocalDateTime.parse(parts[6]);

        EpicTask epicTask = new EpicTask(heading, description, id);
        epicTask.setStatus(status);
        epicTask.setDuration(duration);
        epicTask.setStartTime(startTime);

        return epicTask;
    }
}