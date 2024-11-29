package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static tracker.Status.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    @BeforeEach
    public void setUp() {
        manager = createManager();
    }

    protected abstract T createManager();

    @Test
    public void testAddTask() {
        Task task = new Task("Test Task", "Description", 1, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createTask(task);
        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    public void testRemoveAllTasks() {
        Task task1 = new Task("Test Task 1", "Description", 1, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Test Task 2", "Description", 2, Duration.ofMinutes(45), LocalDateTime.now().plusDays(1));

        manager.createTask(task1);
        manager.createTask(task2);
        manager.removeAllTasks();

        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void testSubtaskShouldBelongToEpic() {
        EpicTask epicTask = new EpicTask("Epic Task", "Epic Description", 1);
        manager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("Subtask", "Subtask Description", 2, epicTask.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        manager.createSubTask(subtask);

        assertTrue(epicTask.getSubtasks().contains(subtask));
    }

    @Test
    public void testEpicStatusCalculation() {
        EpicTask epicTask = new EpicTask("Epic Task", "Epic Description", 1);
        manager.createEpicTask(epicTask);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", 2, epicTask.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", 3, epicTask.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));

        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        subtask1.setStatus(DONE);

        assertEquals(NEW, epicTask.getStatus());
    }

    @Test
    public void testIntervalOverlap() {
        Task task1 = new Task("Task 1", "Description 1", 1, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", 2, Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(15));

        manager.createTask(task1);

        try {
            manager.createTask(task2);
            fail("Expected IllegalArgumentException for overlapping tasks");
        } catch (IllegalArgumentException e) {
            assertEquals("Задача пересекается с существующей задачей.", e.getMessage());
        }
    }

}

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager(new HashMap<>(), new HashMap<>(), new HashMap<>());
    }
}

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static final String FILE_PATH = "test_tasks.csv";

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(new HashMap<>(), new HashMap<>(), new HashMap<>(), FILE_PATH);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(FILE_PATH));
    }

    @Test
    public void testLoadThrowsManagerSaveException() {
        String invalidFilePath = "invalid/path/to/file.txt";

        assertThrows(ManagerSaveException.class, () -> {
            manager.load();
        }, "Ошибка при загрузке данных из файла: " + invalidFilePath);
    }

    @Test
    public void testCreateSubTaskThrowsIllegalArgumentException() {
        EpicTask epic = new EpicTask("Epic 1", "Description 1", 1);
        manager.createEpicTask(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 1, 1, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createSubTask(subtask1);
        Subtask overlappingSubtask = new Subtask("Subtask 2", "Description 2", 2, 1, Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(15));

        assertThrows(IllegalArgumentException.class, () -> {
            manager.createSubTask(overlappingSubtask);
        }, "Подзадача пересекается с существующей подзадачей.");
    }

    @Test
    public void testFromStringThrowsIllegalArgumentException() {
        String invalidString = "1,TASK";
        assertThrows(IllegalArgumentException.class, () -> {
            Subtask.fromString(invalidString);
        }, "Неверный формат строки: " + invalidString);
    }
}