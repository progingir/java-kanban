package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
    void addTask_shouldCreateATask() {
        manager.addTask(task);
        HashMap<Integer, Task> mapOfTasks = manager.getTasks();
        List<Task> listOfTasks = new ArrayList<>(mapOfTasks.values());

        assertNotNull(task.getStatus());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(List.of(task), listOfTasks);
    }

    @Test
    void addTask_shouldNotCreateATaskIfItsEmpty() {
        Task task = manager.addTask(null);
        assertNull(task);
    }

    @Test
    void getTaskById_shouldReturnCreatedTaskById() {
        manager.addTask(task);
        Task task = manager.getTaskById(1);
        HashMap<Integer, Task> mapOfTasks = manager.getTasks();
        List<Task> listOfTasks = new ArrayList<>(mapOfTasks.values());

        assertNotNull(task.getStatus());
        assertEquals(1, task.getId());
        assertEquals(List.of(task), listOfTasks);
    }

    @Test
    void getTaskById_shouldReturnNullIfCreatedTaskIsEmpty() {
        manager.addTask(null);
        Task task = manager.getTaskById(0);
        HashMap<Integer, Task> mapOfTasks = manager.getTasks();

        assertNull(task);
        assertEquals(Collections.EMPTY_MAP, mapOfTasks);
    }

    @Test
    void deleteTasks_shouldDeleteTasks() {
        manager.addTask(task);
        manager.deleteTasks();

        assertEquals(Collections.EMPTY_MAP, manager.getTasks());
        assertEquals(Collections.EMPTY_LIST, manager.getPrioritizedTasks());
    }

    @Test
    void deleteTasks_shouldNotDeleteTasksIfListIsEmpty() {
        // Убедитесь, что вы не добавляете null
        manager.deleteTasks(); // Удаляем задачи
        HashMap<Integer, Task> mapOfTasks = manager.getTasks();
        List<Task> listOfTasks = manager.getPrioritizedTasks();

        assertEquals(0, mapOfTasks.size());
        assertEquals(0, listOfTasks.size());
    }


    @Test
    void deleteTaskById_shouldDeleteTaskById() {
        Task thisTask = manager.addTask(task);
        manager.deleteTaskById(thisTask.getId());
        HashMap<Integer, Task> mapOfTasks = manager.getTasks();
        List<Task> listOfTasks = manager.getPrioritizedTasks();

        assertEquals(0, mapOfTasks.size());
        assertEquals(0, listOfTasks.size());
    }

    @Test
    void deleteTaskById_shouldNotDeleteTaskIfPassedIdIsIncorrect() {
        Task thisTask = manager.addTask(task);
        manager.deleteTaskById(99);
        HashMap<Integer, Task> mapOfTasks = manager.getTasks();
        List<Task> listOfTasks = new ArrayList<>(mapOfTasks.values());

        assertEquals(List.of(thisTask), listOfTasks);
    }

    @Test
    void getTasks_shouldReturnMapOfTasks() {
        manager.addTask(task);
        HashMap<Integer, Task> mapOfTasks = manager.getTasks();

        assertNotNull(mapOfTasks);
        assertEquals(1, mapOfTasks.size());
    }

    @Test
    void updateTask_shouldUpdateTaskToStatusIN_PROGRESS() {
        Task thisTask = manager.addTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(thisTask);

        assertEquals(Status.IN_PROGRESS, manager.getTaskById(thisTask.getId()).getStatus());
    }

    @Test
    void updateTask_shouldUpdateTaskToStatusDONE() {
        Task thisTask = manager.addTask(task);
        task.setStatus(Status.DONE);
        manager.updateTask(thisTask);

        assertEquals(Status.DONE, manager.getTaskById(thisTask.getId()).getStatus());
    }

    @Test
    void updateTask_shouldNotUpdateTaskIfItsEmpty() {
        Task thisTask = manager.addTask(task);
        manager.updateTask(null);

        assertEquals(task, manager.getTaskById(thisTask.getId()));
    }

    // тесты для Подзадач

    @Test
    void addSubtask_shouldCreateASubtask() {
        EpicTask thisEpic = manager.addEpic(epic);
        Subtask thisSubtask = manager.addSubtask(subtask);
        HashMap<Integer, Subtask> mapOfSubtasks = manager.getSubtasks();
        List<Task> listOfSubtasks = new ArrayList<>(mapOfSubtasks.values());

        assertNotNull(thisSubtask.getStatus());
        assertEquals(Status.NEW, thisSubtask.getStatus());
        assertEquals(thisEpic.getId(), thisSubtask.getEpicID());
        assertEquals(List.of(thisSubtask), listOfSubtasks);
        assertEquals(List.of(thisSubtask.getId()), thisEpic.getSubtasksIds());
    }

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
    void deleteSubtaskById_shouldNotDeleteSubtaskIfPassedIdIsIncorrect() {
        manager.addEpic(epic);
        Subtask thisSubtask = manager.addSubtask(subtask);
        manager.deleteSubtaskById(99);
        HashMap<Integer, Subtask> mapOfSubtasks = manager.getSubtasks();
        List<Subtask> listOfSubtasks = new ArrayList<>(mapOfSubtasks.values());

        assertEquals(List.of(thisSubtask), listOfSubtasks);
    }

    @Test
    void getSubtasks_shouldReturnMapOfSubtasks() {
        EpicTask thisEpic = manager.addEpic(epic);
        Subtask thisSubtask = manager.addSubtask(subtask);
        HashMap<Integer, EpicTask> mapOfEpics = manager.getEpics();
        HashMap<Integer, Subtask> mapOfSubtasks = manager.getSubtasks();

        assertNotNull(mapOfSubtasks);
        assertTrue(mapOfSubtasks.containsValue(thisSubtask));
        assertTrue(mapOfEpics.containsValue(thisEpic));
        assertEquals(1, mapOfSubtasks.size());
    }

    @Test
    void getSubtasks_shouldReturnAnEmptyMapOfSubtasks() {
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    void updateSubtask_shouldUpdateSubtaskToStatusIN_PROGRESS() {
        manager.addEpic(epic);
        Subtask thisSubtask = manager.addSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, manager.getSubtaskById(thisSubtask.getId()).getStatus());
    }

    @Test
    void updateSubtask_shouldUpdateSubtaskToStatusDONE() {
        manager.addEpic(epic);
        Subtask thisSubtask = manager.addSubtask(subtask);
        thisSubtask.setStatus(Status.DONE);

        assertEquals(Status.DONE, manager.getSubtaskById(thisSubtask.getId()).getStatus());
    }

    @Test
    void updateSubtask_shouldNotUpdateSubtaskIfItsEmpty() {
        manager.addEpic(epic);
        manager.addSubtask(subtask);
        manager.updateSubtask(null);

        assertEquals(subtask, manager.getSubtaskById(subtask.getId()));
    }

    // тесты для Эпиков

    @Test
    void addEpic_shouldCreateAnEpic() {
        manager.addEpic(epic);
        HashMap<Integer, EpicTask> mapOfEpics = manager.getEpics();
        List<Task> listOfEpics = new ArrayList<>(mapOfEpics.values());

        assertNotNull(epic.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(List.of(epic), listOfEpics);
        assertEquals(Collections.EMPTY_LIST, epic.getSubtasksIds());
    }

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
    void deleteEpics_shouldDeleteEpics() {
        manager.addEpic(epic);
        manager.deleteEpics();

        assertEquals(Collections.EMPTY_MAP, manager.getEpics());
        assertEquals(Collections.EMPTY_LIST, manager.getPrioritizedTasks());
    }

    @Test
    void deleteEpics_shouldNotDeleteEpicIfListIsEmpty() {
        manager.addEpic(epic);
        manager.deleteEpics();
        HashMap<Integer, EpicTask> mapOfEpics = manager.getEpics();
        List<Task> listOfTasks = manager.getPrioritizedTasks();

        assertEquals(0, mapOfEpics.size());
        assertEquals(0, listOfTasks.size());
    }

    @Test
    void deleteEpicById_shouldNotDeleteEpicIfPassedIdIsIncorrect() {
        EpicTask thisEpic = manager.addEpic(epic);
        manager.deleteEpicById(0);
        HashMap<Integer, EpicTask> mapOfEpics = manager.getEpics();
        List<Task> listOfEpics = new ArrayList<>(mapOfEpics.values());

        assertEquals(List.of(thisEpic), listOfEpics);
    }

    @Test
    void getEpics_shouldReturnMapOfEpics() {
        manager.addEpic(epic);
        HashMap<Integer, EpicTask> mapOfEpics = manager.getEpics();

        assertNotNull(mapOfEpics);
        assertEquals(1, mapOfEpics.size());
    }

    @Test
    void getEpics_shouldReturnAnEmptyMapOfEpics() {
        assertTrue(manager.getEpics().isEmpty());
    }

    @Test
    void updateEpic_shouldUpdateEpicToStatusIN_PROGRESS() {
        EpicTask thisEpic = manager.addEpic(epic);
        epic.setStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(thisEpic.getId()).getStatus());
    }

    @Test
    void updateEpic_shouldUpdateEpicToStatusDONE() {
        EpicTask thisEpic = manager.addEpic(epic);
        epic.setStatus(Status.DONE);

        assertEquals(Status.DONE, manager.getEpicById(thisEpic.getId()).getStatus());
    }

    @Test
    void updateEpic_shouldNotUpdateEpicIfItsEmpty() {
        EpicTask thisEpic = manager.addEpic(epic);
        manager.updateEpic(null);

        assertEquals(thisEpic, manager.getEpicById(thisEpic.getId()));
    }

    // остальные публичные методы

    @Test
    void getHistory_shouldReturnListOfHistory() {
        EpicTask thisEpic = manager.addEpic(epic);
        manager.getEpicById(thisEpic.getId());
        List<Task> listOfHistory = manager.getHistory();

        assertEquals(1, listOfHistory.size());
    }

    @Test
    void getHistory_shouldReturnAnEmptyHistoryList() {
        manager.getTaskById(0);
        manager.getEpicById(0);
        manager.getSubtaskById(0);

        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void getPrioritizedTasks_shouldReturnListOfPrioritizedTasks() {
        Task thisTask = manager.addTask(task);
        manager.getTaskById(thisTask.getId());
        List<Task> list = List.of(thisTask);
        List<Task> listOfPrioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(list, listOfPrioritizedTasks);
        assertFalse(listOfPrioritizedTasks.isEmpty());
    }

    @Test
    void getPrioritizedTasks_shouldReturnAnEmptyListOfPrioritizedTasks() {
        assertTrue(manager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void getSubtasksOfEpic_shouldReturnListOfParticularEpicSubtasks() {
        EpicTask thisEpic = manager.addEpic(epic);
        manager.addSubtask(subtask);
        List<Integer> listOfSubtasksIds = thisEpic.getSubtasksIds();

        assertFalse(listOfSubtasksIds.isEmpty());
    }

    @Test
    void getSubtasksOfEpic_shouldReturnAnEmptyListOfParticularEpicSubtasks() {
        EpicTask thisEpic = manager.addEpic(epic);
        List<Integer> listOfSubtasksIds = thisEpic.getSubtasksIds();

        assertTrue(listOfSubtasksIds.isEmpty());
    }
}