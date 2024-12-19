import tracker.Status;
import tracker.HistoryManager;
import tracker.InHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.Task;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InHistoryManagerTest {

    private HistoryManager historyManager;

    private final Task task = new Task(1, "Task 1", "Groceries", 0,
            Instant.ofEpochMilli(1685998800000L), Status.NEW);
    private final Task task2 = new Task(2, "Task 2", "Sport", 0,
            Instant.ofEpochMilli(1686603600000L), Status.IN_PROGRESS);
    private final Task task3 = new Task(3, "Task 3", "Household chores", 0,
            Instant.ofEpochMilli(1686085200000L), Status.DONE);

    @BeforeEach
    void beforeEach() {
        historyManager = new InHistoryManager();
    }

    @Test
    void add_shouldAddTasksToHistoryList() {
        Task thisTask = historyManager.add(task);
        Task thisTask1 = historyManager.add(task2);

        assertEquals(List.of(thisTask, thisTask1), historyManager.getHistory());
    }

    @Test
    void add_shouldReturnNullIfTaskIsEmpty() {
        historyManager.add(null);

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void add_shouldReturnAnEmptyListIfThereIsNoHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void remove_shouldRemoveATaskFromHistory() {
        historyManager.add(task);
        Task thisTask1 = historyManager.add(task2);
        historyManager.remove(1);

        assertEquals(List.of(thisTask1), historyManager.getHistory());
    }

    @Test
    void remove_shouldDoNothingIfPassedIdIsIncorrect() {
        Task thisTask = historyManager.add(task);
        Task thisTask1 = historyManager.add(task2);

        historyManager.remove(3);
        assertEquals(List.of(thisTask, thisTask1), historyManager.getHistory());
    }

    @Test
    void add_shouldNotAddExistingTaskToHistoryList() {
        Task thisTask = historyManager.add(task);
        historyManager.add(task);

        assertEquals(List.of(thisTask), historyManager.getHistory());
    }

    @Test
    void remove_shouldRemoveParticularTaskFromHistoryList() {
        Task thisTask = historyManager.add(task);
        Task thisTask1 = historyManager.add(task2);
        Task thisTask2 = historyManager.add(task3);
        historyManager.remove(thisTask1.getId());

        assertEquals(List.of(thisTask, thisTask2), historyManager.getHistory());
    }

}
