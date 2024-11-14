package tracker;

import java.util.HashMap;


public class Subtask extends Task {
    int epicId;

    public Subtask(String heading, String description, int id, int epicId) {
        super(heading, description, id);
        this.epicId = epicId;
    }

    public EpicTask getEpicTask(HashMap<Integer, EpicTask> epicTaskHashMap) {
        if (epicTaskHashMap.containsKey(epicId)) {
            return epicTaskHashMap.get(epicId);
        } else {
            System.out.println("Эпика с таким айди нет");
            return null;
        }
    }

    @Override
    public String printTask() {
        return "Подзадача с идентификатором " + id + "\n" + "Название: " + heading + "\n" + "Описание: " + description + "\n" + "Статус: " + status + "\n" + "Принадлежит эпику с идентификатором " + epicId + "\n";
    }

    @Override
    public String toString() {
        return getId() + "," + Type.SUB + "," + getHeading() + "," + getStatus() + "," + getDescription() + ","+epicId;
    }

    // Переопределение метода fromString
    public static Subtask fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 6) { // Учитываем, что у Subtask 6 частей
            throw new IllegalArgumentException("Неверный формат строки: " + value);
        }

        int id = Integer.parseInt(parts[0]);
        String heading = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        int epicId = Integer.parseInt(parts[5]); // Извлекаем epicId

        Subtask subtask = new Subtask(heading, description, id, epicId);
        subtask.setStatus(status); // Устанавливаем статус
        return subtask;
    }
}