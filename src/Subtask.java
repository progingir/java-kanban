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
        return "Подзадача с идентификатором " + id + "\n" +
                "Название: " + heading + "\n" +
                "Описание: " + description + "\n" +
                "Статус: " + status + "\n" +
                "Принадлежит эпику с идентификатором " + epicId + "\n";
    }
}