import tracker.Status;
import tracker.Manager;
import org.junit.jupiter.api.Test;
import tracker.Epic;
import tracker.Subtask;
import tracker.Task;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

abstract class ManagerTest<T extends Manager> {
    protected T manager;

    private final Task task = new Task(1, "Task 1", "Groceries", 2,
            Instant.ofEpochMilli(1685998800000L), Status.NEW);
    private final Epic epic = new Epic(3, "Epic 1", "Shopping", 11,
            Instant.ofEpochMilli(1686603600000L), Status.NEW);
    private final Subtask subtask = new Subtask(3, "Subtask 1", "Buy milk", 14,
            Instant.ofEpochMilli(1686085200000L), 12, Status.NEW);

    // тесты для Задач

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
        manager.addTask(null);
        manager.deleteTasks();
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
        Epic thisEpic = manager.addEpic(epic);
        Subtask thisSubtask = manager.addSubtask(subtask);
        HashMap<Integer, Subtask> mapOfSubtasks = manager.getSubtasks();
        List<Task> listOfSubtasks = new ArrayList<>(mapOfSubtasks.values());

        assertNotNull(thisSubtask.getStatus());
        assertEquals(Status.NEW, thisSubtask.getStatus());
        assertEquals(thisEpic.getId(), thisSubtask.getEpicId());
        assertEquals(List.of(thisSubtask), listOfSubtasks);
        assertEquals(List.of(thisSubtask.getId()), thisEpic.getSubtasksIds());
    }

    @Test
    void addSubtask_shouldNotCreateASubtaskIfItsEmpty() {
        manager.addEpic(null);
        Subtask subtask = manager.addSubtask(null);
        HashMap<Integer, Subtask> mapOfSubtasks = manager.getSubtasks();
        List<Subtask> listOfSubtasks = new ArrayList<>(mapOfSubtasks.values());
;
        assertNull(subtask);
        assertTrue(listOfSubtasks.isEmpty());
    }

    @Test
    void getSubtaskById_shouldReturnCreatedSubtaskById() {
        manager.addEpic(epic);
        Subtask thisSubtask = manager.addSubtask(subtask);

        HashMap<Integer, Subtask> mapOfSubtasks = manager.getSubtasks();
        List<Subtask> listOfSubtasks = new ArrayList<>(mapOfSubtasks.values());

        assertNotNull(thisSubtask.getStatus());
        assertEquals(12, thisSubtask.getId());
        assertEquals(List.of(thisSubtask), listOfSubtasks);
    }

    @Test
    void getSubtaskById_shouldReturnNullIfCreatedSubtaskIsEmpty() {
        manager.addSubtask(null);
        Subtask subtask = manager.getSubtaskById(0);

        assertNull(subtask);
    }

    @Test
    void deleteSubtasks_shouldDeleteSubtasks() {
        manager.addSubtask(subtask);
        manager.deleteSubtasks();

        assertEquals(Collections.EMPTY_MAP, manager.getSubtasks());
        assertEquals(Collections.EMPTY_LIST, manager.getPrioritizedTasks());
    }

    @Test
    void deleteSubtasks_shouldNotDeleteSubtasksIfListIsEmpty() {
        manager.addSubtask(subtask);
        manager.deleteSubtasks();
        HashMap<Integer, Subtask> mapOfSubtasks = manager.getSubtasks();
        List<Task> listOfTasks = manager.getPrioritizedTasks();

        assertEquals(0, mapOfSubtasks.size());
        assertEquals(0, listOfTasks.size());
    }

    @Test
    void deleteSubtaskById_shouldDeleteSubtaskById() {
        manager.addSubtask(subtask);
        HashMap<Integer, Subtask> mapOfSubtasks = manager.getSubtasks();
        List<Task> listOfTasks = manager.getPrioritizedTasks();

        assertEquals(0, mapOfSubtasks.size());
        assertEquals(0, listOfTasks.size());
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
        Epic thisEpic = manager.addEpic(epic);
        Subtask thisSubtask = manager.addSubtask(subtask);
        HashMap<Integer, Epic> mapOfEpics = manager.getEpics();
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
        HashMap<Integer, Epic> mapOfEpics = manager.getEpics();
        List<Task> listOfEpics = new ArrayList<>(mapOfEpics.values());

        assertNotNull(epic.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(List.of(epic), listOfEpics);
        assertEquals(Collections.EMPTY_LIST, epic.getSubtasksIds());
    }

    @Test
    void addEpic_shouldNotCreateAnEpicIfItsEmpty() {
        Epic epic = manager.addEpic(null);

        assertNull(epic);
    }

    @Test
    void getEpicById_shouldReturnCreatedEpicById() {
        manager.addEpic(epic);
        Epic epic = manager.getEpicById(3);
        HashMap<Integer, Epic> mapOfEpics = manager.getEpics();
        List<Epic> listOfEpics = new ArrayList<>(mapOfEpics.values());

        assertNotNull(epic.getStatus());
        assertEquals(3, epic.getId());
        assertEquals(List.of(epic), listOfEpics);
    }

    @Test
    void getEpicById_shouldReturnNullIfCreatedEpicIsEmpty() {
        manager.addEpic(null);
        Epic epic = manager.getEpicById(0);

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
        HashMap<Integer, Epic> mapOfEpics = manager.getEpics();
        List<Task> listOfTasks = manager.getPrioritizedTasks();

        assertEquals(0, mapOfEpics.size());
        assertEquals(0, listOfTasks.size());
    }

    @Test
    void deleteEpicById_shouldDeleteEpicById() {
        manager.addEpic(epic);
        manager.deleteEpicById(3);
        HashMap<Integer, Epic> mapOfEpics = manager.getEpics();
        List<Task> listOfTasks = manager.getPrioritizedTasks();

        assertEquals(0, mapOfEpics.size());
        assertEquals(0, listOfTasks.size());
    }

    @Test
    void deleteEpicById_shouldNotDeleteEpicIfPassedIdIsIncorrect() {
        Epic thisEpic = manager.addEpic(epic);
        manager.deleteEpicById(0);
        HashMap<Integer, Epic> mapOfEpics = manager.getEpics();
        List<Task> listOfEpics = new ArrayList<>(mapOfEpics.values());

        assertEquals(List.of(thisEpic), listOfEpics);
    }

    @Test
    void getEpics_shouldReturnMapOfEpics() {
        manager.addEpic(epic);
        HashMap<Integer, Epic> mapOfEpics = manager.getEpics();

        assertNotNull(mapOfEpics);
        assertEquals(1, mapOfEpics.size());
    }

    @Test
    void getEpics_shouldReturnAnEmptyMapOfEpics() {
        assertTrue(manager.getEpics().isEmpty());
    }

    @Test
    void updateEpic_shouldUpdateEpicToStatusIN_PROGRESS() {
        Epic thisEpic = manager.addEpic(epic);
        epic.setStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(thisEpic.getId()).getStatus());
    }

    @Test
    void updateEpic_shouldUpdateEpicToStatusDONE() {
        Epic thisEpic = manager.addEpic(epic);
        epic.setStatus(Status.DONE);

        assertEquals(Status.DONE, manager.getEpicById(thisEpic.getId()).getStatus());
    }

    @Test
    void updateEpic_shouldNotUpdateEpicIfItsEmpty() {
        Epic thisEpic = manager.addEpic(epic);
        manager.updateEpic(null);

        assertEquals(thisEpic, manager.getEpicById(thisEpic.getId()));
    }

    // остальные публичные методы

    @Test
    void getHistory_shouldReturnListOfHistory() {
        Epic thisEpic = manager.addEpic(epic);
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
        Epic thisEpic = manager.addEpic(epic);
        manager.addSubtask(subtask);
        List<Integer> listOfSubtasksIds = thisEpic.getSubtasksIds();

        assertFalse(listOfSubtasksIds.isEmpty());
    }

    @Test
    void getSubtasksOfEpic_shouldReturnAnEmptyListOfParticularEpicSubtasks() {
        Epic thisEpic = manager.addEpic(epic);
        List<Integer> listOfSubtasksIds = thisEpic.getSubtasksIds();

        assertTrue(listOfSubtasksIds.isEmpty());
    }
}
