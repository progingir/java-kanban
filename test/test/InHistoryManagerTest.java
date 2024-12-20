package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InHistoryManagerTest {

    private HistoryManager historyManager;

    // Обновленные параметры задач
    private final Task task = new Task("Task 1", "Groceries", 1, Duration.ofMinutes(30),
            LocalDateTime.of(2023, 6, 13, 10, 0));
    private final Task task2 = new Task("Task 2", "Sport", 2, Duration.ofMinutes(30),
            LocalDateTime.of(2023, 6, 15, 10, 0));
    private final EpicTask epic = new EpicTask("Epic 1", "Go to the shop", 2);
    private final EpicTask epic2 = new EpicTask("Epic 2", "Household chores", 3);
    private final Subtask subtask = new Subtask("Subtask 1", "Buy milk", 3, 2, Duration.ofMinutes(15),
            LocalDateTime.of(2023, 6, 14, 10, 0));
    private final Subtask subtask2 = new Subtask("Subtask 2", "Clean the kitchen", 4, 3, Duration.ofMinutes(20),
            LocalDateTime.of(2023, 6, 16, 10, 0));

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
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
        Task thisTask2 = historyManager.add(subtask);
        historyManager.remove(thisTask1.getId());

        assertEquals(List.of(thisTask, thisTask2), historyManager.getHistory());
    }
}