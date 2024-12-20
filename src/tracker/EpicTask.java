package tracker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EpicTask extends Task {
    private List<Integer> subtasksIds;

    public EpicTask(String heading, String description, int id) {
        super(heading, description, id, Duration.ZERO, null);
        this.subtasksIds = new ArrayList<>();
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    private void calculateEpicDetails(HashMap<Integer, Subtask> subtasks) {
        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStartTime = null;
        LocalDateTime latestEndTime = null;

        for (Integer subtaskId : subtasksIds) {
            Subtask subtask = subtasks.get(subtaskId); // Получаем подзадачу по идентификатору
            if (subtask != null) { // Проверяем, что подзадача существует
                totalDuration = totalDuration.plus(subtask.getDuration());
                if (earliestStartTime == null || (subtask.getStartTime() != null && subtask.getStartTime().isBefore(earliestStartTime))) {
                    earliestStartTime = subtask.getStartTime();
                }
                if (latestEndTime == null || (subtask.getEndTime() != null && subtask.getEndTime().isAfter(latestEndTime))) {
                    latestEndTime = subtask.getEndTime();
                }
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

        // Проверка на минимальное количество частей
        if (parts.length < 6) {
            throw new IllegalArgumentException("Неверный формат строки: " + value);
        }

        int id = Integer.parseInt(parts[0]);
        String heading = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        Duration duration = Duration.ofMinutes(Long.parseLong(parts[5]));
        LocalDateTime startTime = parts.length > 6 && !parts[6].isEmpty() ? LocalDateTime.parse(parts[6]) : null;

        EpicTask epicTask = new EpicTask(heading, description, id);
        epicTask.setStatus(status);
        epicTask.setDuration(duration);
        epicTask.setStartTime(startTime);

        // Инициализация пустого списка подзадач, если у эпика нет подзадач
        if (duration.isZero() && startTime == null) {
            epicTask.subtasksIds = new ArrayList<>(); // Инициализируем пустой список подзадач
        }

        return epicTask;
    }
}