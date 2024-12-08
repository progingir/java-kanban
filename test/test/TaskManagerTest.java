package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    @BeforeEach
    public void setUp() {
        manager = createManager();
    }

    protected abstract T createManager();

    @Test
    public void getAllTasksShouldReturnAllTasks() {
        Task task1 = new Task("Task 1", "Description 1", 1, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", 2, Duration.ofMinutes(45), LocalDateTime.now().plusDays(1));
        manager.createTask(task1);
        manager.createTask(task2);

        assertEquals(2, manager.getAllTasks().size(), "Должно быть 2 задачи.");
    }

    @Test
    public void getAllEpicTasksShouldReturnAllEpicTasks() {
        EpicTask epicTask1 = new EpicTask("Epic Task 1", "Description 1", 1);
        EpicTask epicTask2 = new EpicTask("Epic Task 2", "Description 2", 2);
        manager.createEpicTask(epicTask1);
        manager.createEpicTask(epicTask2);

        assertEquals(2, manager.getAllEpicTasks().size(), "Должно быть 2 эпические задачи.");
    }

    @Test
    public void getAllSubTasksShouldReturnAllSubTasks() {
        EpicTask epicTask = new EpicTask("Epic Task", "Description", 1);
        manager.createEpicTask(epicTask);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", 2, epicTask.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", 3, epicTask.getId(), Duration.ofMinutes(45), LocalDateTime.now().plusHours(1));
        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        assertEquals(2, manager.getAllSubTasks().size(), "Должно быть 2 подзадачи.");
    }

    @Test
    public void removeAllTasksShouldClearAllTasks() {
        Task task = new Task("Task", "Description", 1, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createTask(task);
        manager.removeAllTasks();

        assertEquals(0, manager.getAllTasks().size(), "Количество задач должно быть 0 после удаления.");
    }

    @Test
    public void removeAllEpicTasksShouldClearAllEpicTasks() {
        EpicTask epicTask = new EpicTask("Epic Task", "Description", 1);
        manager.createEpicTask(epicTask);
        manager.removeAllEpicTasks();

        assertEquals(0, manager.getAllEpicTasks().size(), "Количество эпических задач должно быть 0 после удаления.");
    }

    @Test
    public void removeAllSubTasksShouldClearAllSubTasks() {
        EpicTask epicTask = new EpicTask("Epic Task", "Description", 1);
        manager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("Subtask", "Description", 2, epicTask.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        manager.createSubTask(subtask);
        manager.removeAllSubTasks();

        assertEquals(0, manager.getAllSubTasks().size(), "Количество подзадач должно быть 0 после удаления.");
    }

    @Test
    public void getTaskByIdShouldReturnCorrectTask() {
        Task task = new Task("Task", "Description", 1, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createTask(task);
        Task retrievedTask = manager.getTaskById(task.getId());

        assertEquals(task, retrievedTask, "Должен вернуть правильную задачу по ID.");
    }

    @Test
    public void getEpicTaskByIdShouldReturnCorrectEpicTask() {
        EpicTask epicTask = new EpicTask("Epic Task", "Description", 1);
        manager.createEpicTask(epicTask);
        EpicTask retrievedEpicTask = manager.getEpicTaskById(epicTask.getId());

        assertEquals(epicTask, retrievedEpicTask, "Должен вернуть правильную эпическую задачу по ID.");
    }

    @Test
    public void getSubTaskByIdShouldReturnCorrectSubtask() {
        EpicTask epicTask = new EpicTask("Epic Task", "Description", 1);
        manager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("Subtask", "Description", 2, epicTask.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        manager.createSubTask(subtask);
        Subtask retrievedSubtask = manager.getSubTaskById(subtask.getId());

        assertEquals(subtask, retrievedSubtask, "Должен вернуть правильную подзадачу по ID.");
    }

    @Test
    public void createTaskShouldAddTask() {
        Task task = new Task("Task", "Description", 1, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createTask(task);

        assertEquals(1, manager.getAllTasks().size(), "Количество задач должно быть 1 после добавления.");
    }

    @Test
    public void createEpicTaskShouldAddEpicTask() {
        EpicTask epicTask = new EpicTask("Epic Task", "Description", 1);
        manager.createEpicTask(epicTask);

        assertEquals(1, manager.getAllEpicTasks().size(), "Количество эпических задач должно быть 1 после добавления.");
    }

    @Test
    public void createSubTaskShouldAddSubtask() {
        EpicTask epicTask = new EpicTask("Epic Task", "Description", 1);
        manager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("Subtask", "Description", 2, epicTask.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        manager.createSubTask(subtask);

        assertEquals(1, manager.getAllSubTasks().size(), "Количество подзадач должно быть 1 после добавления.");
    }

    @Test
    public void updateTaskShouldChangeTaskDetails() {
        Task task = new Task("Task", "Description", 1, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createTask(task);
        manager.updateTask(task.getId(), 1, "Updated Task");

        assertEquals("Updated Task", manager.getTaskById(task.getId()).getHeading(), "Заголовок задачи должен быть обновлен.");
    }

    @Test
    public void removeSubTaskByIdShouldRemoveSubtask() {
        EpicTask epicTask = new EpicTask("Epic Task", "Description", 1);
        manager.createEpicTask(epicTask);
        Subtask subtask = new Subtask("Subtask", "Description", 2, epicTask.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        manager.createSubTask(subtask);
        manager.removeTaskById(subtask.getId());

        assertNull(manager.getSubTaskById(subtask.getId()), "Подзадача должна быть удалена.");
    }

    @Test
    public void getHistoryShouldReturnTaskHistory() {
        Task task1 = new Task("Task 1", "Description 1", 1, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", 2, Duration.ofMinutes(45), LocalDateTime.now().plusDays(1));
        manager.createTask(task1);
        manager.createTask(task2);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        assertEquals(2, manager.getHistory().size(), "История должна содержать 2 задачи.");
    }
}