package test;

import org.junit.jupiter.api.Test;
import tracker.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tracker.Status.*;

class EpicTaskTest {
    String epicHeading = "first epic";
    String epicDescription = "first description";

    String subHeading = "first subtask";
    String subDescription = "first subtask description";

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    HashMap<Integer, ArrayList<Subtask>> subTasks = new HashMap<>();

    InMemoryTaskManager manager = new InMemoryTaskManager(tasks, epicTasks, subTasks);

    @Test
    public void getSubtasksTaskShouldReturnListOfSubtasks() {
        EpicTask epicTask = new EpicTask(epicHeading, epicDescription, 0);
        manager.createEpicTask(epicTask);

        int subtaskId = manager.getTaskIndex(subHeading, subDescription, "subtask");
        Subtask subtask = new Subtask(subHeading, subDescription, subtaskId, epicTask.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        manager.createSubTask(subtask);

        ArrayList<Subtask> expectedSubtasks = new ArrayList<>();
        expectedSubtasks.add(subtask);

        assertEquals(expectedSubtasks, manager.getSubtasks(epicTask.getId()));
    }

    @Test
    public void getStatusShouldReturnEpicStatusAllNew() {
        EpicTask epic = new EpicTask("Epic Task", "Epic Description", 1);
        manager.createEpicTask(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", 2, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", 3, epic.getId(), Duration.ofMinutes(45), LocalDateTime.now().plusDays(1));
        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        assertEquals(NEW, epic.getStatus());
    }

    @Test
    public void checkStatusShouldReturnEpicStatusAllDone() {
        EpicTask epic = new EpicTask("Epic Task", "Epic Description", 1);
        manager.createEpicTask(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", 2, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", 3, epic.getId(), Duration.ofMinutes(45), LocalDateTime.now().plusDays(1));

        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);

        manager.checkStatus(epic.getId(), 1);

        assertEquals(Status.DONE, epic.getStatus(), "Статус эпической задачи должен быть DONE.");
    }


    @Test
    public void checkStatusShouldReturnEpicStatusMixed() {
        EpicTask epic = new EpicTask("Epic Task", "Epic Description", 1);
        manager.createEpicTask(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", 2, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", 3, epic.getId(), Duration.ofMinutes(45), LocalDateTime.now().plusDays(1));
        subtask1.setStatus(DONE);
        subtask2.setStatus(NEW);
        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        manager.checkStatus(epic.getId(), 1);

        assertEquals(IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void checkStatusShouldReturnEpicStatusInProgress() {
        EpicTask epic = new EpicTask("Epic Task", "Epic Description", 1);
        manager.createEpicTask(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", 2, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.now());
        subtask1.setStatus(IN_PROGRESS);
        manager.createSubTask(subtask1);

        manager.checkStatus(epic.getId(), 1);

        assertEquals(IN_PROGRESS, epic.getStatus());
    }
}