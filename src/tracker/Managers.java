package tracker;

public class Managers {

    public static Manager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Manager getDefault(String url) {
        return new HttpTaskManager(getDefaultHistory(), url, new KVTaskClient(url));
    }

}
