package test;

import org.junit.jupiter.api.BeforeEach;
import tracker.Status;
import tracker.Manager;
import org.junit.jupiter.api.Test;
import tracker.EpicTask;
import tracker.Subtask;
import tracker.Task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

abstract class ManagerTest<T extends Manager> {
    protected T manager;

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

    // тесты для Задач

    @BeforeEach
    void setUp() throws Exception {
        // Указываем путь к файлу
        Path filePath = Paths.get("resources/back up.csv");

        // Создаем родительскую директорию, если она не существует
        Files.createDirectories(filePath.getParent());

        // Создаем файл, если он не существует
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
    }


    @Test
    void addTask_shouldNotCreateATaskIfItsEmpty() {
        Task task = manager.addTask(null);
        assertNull(task);
    }


    // тесты для Подзадач


    @Test
    void addSubtask_shouldNotCreateASubtaskIfItsEmpty() {
        manager.addEpic(null);
        Subtask subtask = manager.addSubtask(null);
        HashMap<Integer, Subtask> mapOfSubtasks = manager.getSubtasks();
        List<Subtask> listOfSubtasks = new ArrayList<>(mapOfSubtasks.values());

        assertNull(subtask);
        assertTrue(listOfSubtasks.isEmpty());
    }

    @Test
    void getSubtaskById_shouldReturnNullIfCreatedSubtaskIsEmpty() {
        manager.addSubtask(null);
        Subtask subtask = manager.getSubtaskById(0);

        assertNull(subtask);
    }


    @Test
    void getSubtasks_shouldReturnAnEmptyMapOfSubtasks() {
        assertTrue(manager.getSubtasks().isEmpty());
    }


    // тесты для Эпиков


    @Test
    void addEpic_shouldNotCreateAnEpicIfItsEmpty() {
        EpicTask epic = manager.addEpic(null);

        assertNull(epic);
    }

    @Test
    void getEpicById_shouldReturnNullIfCreatedEpicIsEmpty() {
        manager.addEpic(null);
        EpicTask epic = manager.getEpicById(0);

        assertNull(epic);
    }


    @Test
    void getEpics_shouldReturnAnEmptyMapOfEpics() {
        assertTrue(manager.getEpics().isEmpty());
    }


}
