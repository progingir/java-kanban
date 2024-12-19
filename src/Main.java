import tracker.*;

import java.io.IOException;
import java.time.Instant;

public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();

        HttpTaskManager httpManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
        new HttpTaskServer(httpManager).start();

        httpManager.addTask(new Task(1, "Task 1", "Groceries", 2,
                Instant.ofEpochMilli(1686603600000L), Status.NEW));
        httpManager.addTask(new Task(2, "Task 2", "Sport", 10,
                Instant.ofEpochMilli(1686690000000L), Status.IN_PROGRESS));

        Epic epic1 = new Epic(3, "Epic 1", "Shopping", 11,
                Instant.ofEpochMilli(1686776400000L), Status.NEW);
        httpManager.addEpic(epic1);
        httpManager.addSubtask(new Subtask(3, "Subtask 1", "Buy milk", 12, Status.IN_PROGRESS));
        httpManager.addSubtask(new Subtask(3, "Subtask 2", "Buy bread", 9, Status.IN_PROGRESS));
        httpManager.addSubtask(new Subtask(4, "Subtask 3", "Ride for 25 km", 11, Status.NEW));

        Epic epic2 = new Epic(6, "Epic 3", "Household chores", 30,
                Instant.ofEpochMilli(1686517200000L), Status.DONE);
        httpManager.addEpic(epic2);

        httpManager.getTaskById(5);
        httpManager.getEpicById(3);
        httpManager.getSubtaskById(11);

        httpManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
        httpManager.loadFromServer();

        System.out.println(httpManager.getEpics());
        System.out.println(httpManager.getHistory());
        System.out.println(httpManager.getPrioritizedTasks());
    }
}
