package tracker;

public class Managers {

    public static Manager getDefault() {
        return new InMemoryManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InHistoryManager();
    }

    public static Manager getDefault(String url) {
        return new HttpTaskManager(getDefaultHistory(), url, new KVTaskClient(url));
    }

}
