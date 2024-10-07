import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

class InMemoryTaskManagerTest {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    HashMap<Integer, ArrayList<Subtask>> subTasks = new HashMap<>();
    InMemoryTaskManager manager = new InMemoryTaskManager(tasks, epicTasks, subTasks);
    int id;


    String heading = "task";
    String description = "task description";
    Task task = new Task(heading, description, id);

    @Test
    public void tasksShouldHaveSameIdWhenTwoTasksAreSimilar() {
        String heading1 = "task";
        String description1 = "description";
        String heading2 = "task";
        String description2 = "description";

        int id1 = manager.getTaskIndex(heading1, description1, "task");
        int id2 = manager.getTaskIndex(heading2, description2, "task");

        Assertions.assertEquals(id1, id2);
    }

    @Test
    public void shouldReturnTaskByID() {
        id = manager.getTaskIndex(heading, description, "task");
        tasks.put(task.id, task);

        Assertions.assertEquals(task, manager.getTaskById(task.id));
    }

    @Test
    public void shouldReturnEpicTaskByID() {
        id = manager.getTaskIndex(heading, description, "epic task");
        EpicTask epictask = new EpicTask(heading, description, id);
        epicTasks.put(epictask.id, epictask);

        Assertions.assertEquals(epictask, manager.getEpicTaskById(epictask.id));
    }

    @Test
    public void shouldReturnSubtaskByID() {
        id = manager.getTaskIndex(heading, description, "epic task");
        EpicTask epictask = new EpicTask(heading, description, id);

        String heading1 = "subtask";
        String description1 = "subtask description";
        id = manager.getTaskIndex(heading1, description1, "subtask");
        Subtask task = new Subtask(heading1, description1, id, epictask.id);

        ArrayList<Subtask> subtaskArrayList = new ArrayList<>();
        subtaskArrayList.add(task);
        subTasks.put(epictask.id, subtaskArrayList);

        Assertions.assertEquals(task, manager.getSubTaskById(task.id));
    }

    @Test
    public void taskShouldBeUnchangedAcrossAllFields() {
        id = manager.getTaskIndex(heading, description, "task");
        Task task = new Task(heading, description, id);
        manager.createTask(task);
        tasks.put(task.id, task);

        Task fetchedTask = manager.getTaskById(task.id);

        Assertions.assertEquals(task.heading, fetchedTask.heading);
        Assertions.assertEquals(task.description, fetchedTask.description);
        Assertions.assertEquals(task.id, fetchedTask.id);
        Assertions.assertEquals(task.status, fetchedTask.status);
    }

    @Test
    public void epicTaskShouldBeUnchangedAcrossAllFields() {
        id = manager.getTaskIndex(heading, description, "epic task");
        EpicTask task = new EpicTask(heading, description, id);
        manager.createEpicTask(task);
        epicTasks.put(task.id, task);

        EpicTask fetchedTask = manager.getEpicTaskById(task.id);

        Assertions.assertEquals(task.heading, fetchedTask.heading);
        Assertions.assertEquals(task.description, fetchedTask.description);
        Assertions.assertEquals(task.id, fetchedTask.id);
        Assertions.assertEquals(task.status, fetchedTask.status);
    }

    @Test
    public void subTaskShouldBeUnchangedAcrossAllFields() {
        id = manager.getTaskIndex(heading, description, "epic task");
        EpicTask epictask = new EpicTask(heading, description, id);
        epicTasks.put(epictask.id, epictask);

        String heading1 = "subtask";
        String description1 = "subtask description";
        id = manager.getTaskIndex(heading1, description1, "subtask");
        Subtask task = new Subtask(heading1, description1, id, epictask.id);

        manager.createSubTask(task);
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>();
        subtaskArrayList.add(task);
        subTasks.put(epictask.id, subtaskArrayList);

        Subtask fetchedTask = manager.getSubTaskById(task.id);

        Assertions.assertEquals(task.heading, fetchedTask.heading);
        Assertions.assertEquals(task.description, fetchedTask.description);
        Assertions.assertEquals(task.id, fetchedTask.id);
        Assertions.assertEquals(task.epicId, fetchedTask.epicId);
        Assertions.assertEquals(task.status, fetchedTask.status);
    }
}