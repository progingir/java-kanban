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

    @Override
    public String printTask() {
        return "Глобальная задача с идентификатором " + id + "\n" +
                "Название: " + heading + "\n" +
                "Описание: " + description + "\n" +
                "Статус: " + status + "\n";
    }
}


