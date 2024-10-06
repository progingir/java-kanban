import java.util.ArrayList;

public class Managers {

    private static TaskManager taskManager;
    private static InMemoryHistoryManager historyManager;

    public static TaskManager getDefault() {
        return taskManager;
    }

    public static InMemoryHistoryManager getDefaultHistory(){
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager(new ArrayList<>());
        }
        return historyManager;
    }
}
