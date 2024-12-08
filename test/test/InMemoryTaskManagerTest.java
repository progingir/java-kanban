package test;

import org.junit.jupiter.api.BeforeEach;
import tracker.*;

import java.util.ArrayList;
import java.util.HashMap;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, EpicTask> epicTasks;
    private HashMap<Integer, ArrayList<Subtask>> subTasks;

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager(tasks, epicTasks, subTasks);
    }

    @BeforeEach
    public void setUp() {
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
        super.setUp();
    }
}
