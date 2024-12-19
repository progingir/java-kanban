package tracker;

import com.google.gson.*;

import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedManager {
    private final KVTaskClient taskClient;
    private static final Gson gson = new Gson();

    public HttpTaskManager(HistoryManager historyManager, String url, KVTaskClient taskClient) {
        super(historyManager, url);
        this.taskClient = taskClient;
    }

    @Override
    public void save() {
        taskClient.put("task", gson.toJson(tasks.values()));
        taskClient.put("subtask", gson.toJson(subtasks.values()));
        taskClient.put("epic", gson.toJson(epics.values()));
        taskClient.put("tasks", gson.toJson(getPrioritizedTasks()));
        List<Integer> historyIds = getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        taskClient.put("history", gson.toJson(historyIds));
    }

    public void loadFromServer() {
        loadTasks("task");
        loadTasks("subtask");
        loadTasks("epic");
        loadHistory();
    }

    private void loadTasks(String key) {
        JsonElement jsonElement = JsonParser.parseString(taskClient.load(key));
        JsonArray jsonTasksArray = jsonElement.getAsJsonArray();
        for (JsonElement element : jsonTasksArray) {
            Task task;
            Epic epic;
            Subtask subtask;
            switch (key) {
                case "task":
                    task = gson.fromJson(element.getAsJsonObject(), Task.class);
                    tasks.put(task.getId(), task);
                    addTaskToPrioritizedList(task);
                    break;
                case "subtask":
                    subtask = gson.fromJson(element.getAsJsonObject(), Subtask.class);
                    subtasks.put(subtask.getId(), subtask);
                    addTaskToPrioritizedList(subtask);
                    break;
                case "epic":
                    epic = gson.fromJson(element.getAsJsonObject(), Epic.class);
                    epics.put(epic.getId(), epic);
                    addTaskToPrioritizedList(epic);
                    break;
                default:
                    System.out.println("Unable to upload tasks");
                    return;
            }
        }
    }

    private void loadHistory() {
        JsonElement jsonElement = JsonParser.parseString(taskClient.load("history"));
        JsonArray jsonHistoryArray = jsonElement.getAsJsonArray();
        for (JsonElement element : jsonHistoryArray) {
            int id = element.getAsInt();
            if (tasks.containsKey(id)) {
                historyManager.add(tasks.get(id));
            } else if (epics.containsKey(id)) {
                historyManager.add(epics.get(id));
            } else if (subtasks.containsKey(id)) {
                historyManager.add(subtasks.get(id));
            }
        }
    }
}