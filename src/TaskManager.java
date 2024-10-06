import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    ArrayList<EpicTask> getAllEpicTasks();

    List<Subtask> getAllSubTasks();

    void removeAllTasks();

    void removeAllEpicTasks();

    void removeAllSubTasks();

    Task getTaskById(int id);

    EpicTask getEpicTaskById(int id);

    Subtask getSubTaskById(int id);

    Task createTask(Task task);

    EpicTask createEpicTask(EpicTask epicTask);

    Subtask createSubTask(Subtask subtask);

    void updateTask(int id, int comm, String change);

    boolean removeTaskById(int id);

    ArrayList<Task> getHistory();
}
