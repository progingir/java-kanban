import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager = Managers.getDefaultHistory();
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    HashMap<Integer, ArrayList<Subtask>> subTasks = new HashMap<>();
    InMemoryTaskManager manager = new InMemoryTaskManager(tasks, epicTasks, subTasks);
    int id;

    @Test
    void historyShouldContainTask() {
        String heading1 = "subtask";
        String description1 = "subtask description";
        id = manager.getTaskIndex(heading1, description1);
        Task task = new Task(heading1, description1, id);

        historyManager.add(task);
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");

        manager.removeTaskById(0);
        assertEquals(1, history.size(), "История содержит информацию об удаленной задаче");
    }
}