package tracker;

import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<Subtask> subtasks;

    public EpicTask(String heading, String description, int id) {
        super(heading, description, id);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String printTask() {
        return "Глобальная задача с идентификатором " + id + "\n" + "Название: " + heading + "\n" + "Описание: " + description + "\n" + "Статус: " + status + "\n";
    }

    @Override
    public String toString() {
        return getId() + "," + Type.EPIC + "," + getHeading() + "," + getStatus() + "," + getDescription() + ",";
    }

    public static EpicTask fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Неверный формат строки: " + value);
        }

        int id = Integer.parseInt(parts[0]);
        String heading = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        EpicTask epicTask = new EpicTask(heading, description, id);
        epicTask.setStatus(status);

        return epicTask;
    }
}