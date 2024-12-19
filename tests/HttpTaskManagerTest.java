import tracker.HttpTaskManager;
import tracker.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.Managers;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends ManagerTest<HttpTaskManager> {
    private static KVServer server;
    private final Task task = new Task(1, "Task 1", "Groceries", 2,
            Instant.ofEpochMilli(1685998800000L), Status.NEW);
    private final Epic epic = new Epic(3, "Epic 1", "Shopping", 11,
            Instant.ofEpochMilli(1686603600000L), Status.DONE);
    private final Subtask subtask = new Subtask(3, "Subtask 1", "Buy milk", 14,
            Instant.ofEpochMilli(1686085200000L), 12, Status.IN_PROGRESS);

    @BeforeEach
    void setManager() {
        manager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
    }

    @BeforeAll
    static void startServer() throws IOException {
        server = new KVServer();
        server.start();
    }

    @Test
    void shouldLoadFromServer() {
        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);
        manager.getTaskById(task.getId());
        manager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
        manager.loadFromServer();

        assertEquals(2, manager.getPrioritizedTasks().size());
        assertEquals(1, manager.getHistory().size());
        assertEquals(task.toStringFromFile(), manager.getTaskById(task.getId()).toStringFromFile());
        assertEquals(epic.toStringFromFile(), manager.getEpicById(epic.getId()).toStringFromFile());
        assertEquals(subtask.toStringFromFile(), manager.getSubtaskById(subtask.getId()).toStringFromFile());
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
}