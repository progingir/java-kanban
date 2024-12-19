import tracker.Status;
import tracker.Managers;
import tracker.FileBackedManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.Epic;
import tracker.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedManagerTest extends ManagerTest<FileBackedManager> {
    private final Path path = Path.of("resources/back up.csv");
    private final File file = new File(String.valueOf(path));

    private final Task task = new Task(1, "Task 1", "Groceries", 0,
            Instant.ofEpochMilli(1686344400000L), Status.NEW);
    private final Epic epic = new Epic(2, "Epic 1", "Go to the shop", 0,
            Instant.ofEpochMilli(1686430800000L), Status.NEW);

    @BeforeEach
    void beforeEach() {
        manager = new FileBackedManager(Managers.getDefaultHistory(), "resources/back up.csv");
    }

    @AfterEach
    void afterEach() {
        try {
            Files.delete(path);
        } catch (IOException exception) {
            exception.getStackTrace();
        }
    }

    @Test
    void save_loadFromFile_shouldSaveAndLoadCorrectly() {
        Task thisTask = manager.addTask(task);
        Epic thisEpic = manager.addEpic(epic);
        manager.loadFromFile();
        HashMap<Integer, Task> mapOfTasks = manager.getTasks();
        List<Task> listOfTasks = new ArrayList<>(mapOfTasks.values());
        HashMap<Integer, Epic> mapOfEpics = manager.getEpics();
        List<Epic> listOfEpics = new ArrayList<>(mapOfEpics.values());

        assertEquals(List.of(thisTask), listOfTasks);
        assertEquals(List.of(thisEpic), listOfEpics);
    }

    @Test
    void save_loadFromFile_shouldSaveAndLoadEmptyKindOfTasks() {
        manager.save();
        manager.loadFromFile();

        assertEquals(Collections.EMPTY_MAP, manager.getTasks());
        assertEquals(Collections.EMPTY_MAP, manager.getEpics());
        assertEquals(Collections.EMPTY_MAP, manager.getSubtasks());
    }

    @Test
    void save_loadFromFile_shouldSaveAndLoadEmptyHistory() {
        manager.save();
        manager.loadFromFile();

        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

}
