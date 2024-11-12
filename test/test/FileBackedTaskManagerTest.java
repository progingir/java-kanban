package test;

import org.junit.jupiter.api.*;
import tracker.FileBackedTaskManager;
import tracker.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private static final String FILE_PATH = "test_tasks.csv";
    private FileBackedTaskManager manager;

    @BeforeEach
    public void setUp() {
        manager = new FileBackedTaskManager(new HashMap<>(), new HashMap<>(), new HashMap<>(), FILE_PATH);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(FILE_PATH));
    }

    @Test
    public void testCreateTask() {
        Task task = new Task("Test Task", "Description", 1);
        Task createdTask = manager.createTask(task);

        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getHeading());
        assertEquals("Description", createdTask.getDescription());
        assertTrue(new File(FILE_PATH).exists());
    }

    @Test
    public void testSaveAndLoad() {
        Task task = new Task("Test Task", "Description", 1);
        manager.createTask(task);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new File(FILE_PATH));
        Task loadedTask = loadedManager.getTaskById(1);

        assertNotNull(loadedTask);
        assertEquals("Test Task", loadedTask.getHeading());
        assertEquals("Description", loadedTask.getDescription());
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Test Task", "Description", 1);
        manager.createTask(task);
        manager.updateTask(1, 1, "Updated Task");

        Task updatedTask = manager.getTaskById(1);
        assertEquals("Updated Task", updatedTask.getHeading());
    }

    @Test
    public void testRemoveAllTasks() {
        Task task1 = new Task("Test Task 1", "Description", 1);
        Task task2 = new Task("Test Task 2", "Description", 2);
        manager.createTask(task1);
        manager.createTask(task2);

        manager.removeAllTasks();

        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void testLoadFromFile() throws IOException {
        String data = "id,type,name,status,description,epic\n" +
                "1,TASK,Test Task,NEW,Description,0\n" +
                "2,EPIC,Test Epic,NEW,Description,0\n" +
                "3,SUBTASK,Test Subtask,NEW,Description,2\n";
        Files.write(Paths.get(FILE_PATH), data.getBytes());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new File(FILE_PATH));
        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpicTasks().size());
        assertEquals(1, loadedManager.getAllSubTasks().size());
    }
}