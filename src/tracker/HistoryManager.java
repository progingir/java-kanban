package tracker;

import java.util.List;

public interface HistoryManager {

    Task add(Task task);

    List<Task> getHistory();

    void remove(int id);
}