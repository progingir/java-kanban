package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tracker.EpicTask;
import tracker.InMemoryTaskManager;
import tracker.Subtask;
import tracker.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

class SubtaskTest {
    String epicHeading = "second epic";
    String epicDescription = "second description";

    String subHeading_ = "third subtask";
    String subDescription_ = "third subtask description";

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    HashMap<Integer, ArrayList<Subtask>> subTasks = new HashMap<>();
    InMemoryTaskManager manager = new InMemoryTaskManager(tasks, epicTasks, subTasks);

    @Test
    public void getEpicTaskShouldReturnEpicAssociatedWithSubtask() {
        int epicId = manager.getTaskIndex(epicHeading, epicDescription, "epic task");
        EpicTask epic = new EpicTask(epicHeading, epicDescription, epicId);
        epicTasks.put(epic.getId(), epic);

        String subHeading = "second subtask";
        String subDescription = "second subtask description";
        int subtaskId = manager.getTaskIndex(subHeading, subDescription, "subtask");
        Subtask subtask = new Subtask(subHeading, subDescription, subtaskId, epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());

        ArrayList<Subtask> subtaskList = new ArrayList<>();
        subtaskList.add(subtask);
        subTasks.put(epic.getId(), subtaskList);

        Assertions.assertEquals(epic, subtask.getEpicTask(epicTasks), "Метод должен вернуть связанный эпик для сабтаска.");
    }

    @Test
    public void getEpicTaskShouldReturnNullIfEpicIsNotInTheMap() {
        int epicId = manager.getTaskIndex(epicHeading, epicDescription, "epic task");
        EpicTask epic = new EpicTask(epicHeading, epicDescription, epicId);
        epicTasks.put(epic.getId(), epic);

        int subtaskId = manager.getTaskIndex(subHeading_, subDescription_, "subtask");
        Subtask subtaskSecond = new Subtask(subHeading_, subDescription_, subtaskId, 78, Duration.ofMinutes(30), LocalDateTime.now());

        Assertions.assertNull(subtaskSecond.getEpicTask(epicTasks), "Метод должен вернуть null, если эпик не найден.");
    }
}