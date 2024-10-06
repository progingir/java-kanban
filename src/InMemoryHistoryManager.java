import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    ArrayList<Task> history;

    public InMemoryHistoryManager(ArrayList<Task> history){
        this.history = history;
    }

    @Override
    public void add(Task task){
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory(){
        return history;
    }

    public void checkHistory() {
        if (history.size() >= 10) {
            history.removeFirst();
        }
    }
}