package tracker;

import java.util.ArrayList;
import java.util.HashMap;

public class Managers {

    private static TaskManager taskManager;
    private static InMemoryHistoryManager historyManager;

    public static TaskManager getDefault() {
        if (taskManager == null) {
            taskManager = new InMemoryTaskManager(new HashMap<>(), new HashMap<>(), new HashMap<>());
        }
        return taskManager;
    }

    public static InMemoryHistoryManager getDefaultHistory(){
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager(new ArrayList<>());
        }
        return historyManager;
    }
}