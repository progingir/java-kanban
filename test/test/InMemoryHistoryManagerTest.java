package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import tracker.*;
import java.util.ArrayList;
import java.util.HashMap;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager = Managers.getDefaultHistory();
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    HashMap<Integer, ArrayList<Subtask>> subTasks = new HashMap<>();
    InMemoryTaskManager manager = new InMemoryTaskManager(tasks, epicTasks, subTasks);

    @Test
    void historyShouldContainOnlyLastAddedTask() {
        String heading1 = "task1";
        String description1 = "task1 description";
        Task task1 = new Task(heading1, description1, 1);
        historyManager.add(task1);

        String heading2 = "task2";
        String description2 = "task description2";
        Task task2 = new Task(heading2, description2, 2);
        historyManager.add(task2);

        String heading3 = "task3";
        String description3 = "task description3";
        Task task3 = new Task(heading3, description3, 3);
        historyManager.add(task3);

        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История содержит три задачи.");
        assertEquals(task3, history.get(2), "Последняя задача в истории");
        assertEquals(task2, history.get(1), "Вторая задача в истории");
        assertEquals(task1, history.get(0), "Первая задача в истории");

        manager.removeTaskById(0);
        assertEquals(3, history.size(), "История содержит информацию об удаленной задаче");
    }
}
