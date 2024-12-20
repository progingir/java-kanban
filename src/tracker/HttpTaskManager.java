package tracker;

import com.google.gson.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTaskManager {
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
            EpicTask epic;
            Subtask subtask;
            switch (key) {
                case "task":
                    task = gson.fromJson(element.getAsJsonObject(), Task.class);
                    if (task.getStartTime() == null) {
                        task.setStartTime(LocalDateTime.now());
                    }
                    if (task.getDuration() == null) {
                        task.setDuration(Duration.ZERO);
                    }
                    tasks.put(task.getId(), task);
                    addTaskToPrioritizedList(task);
                    break;

                case "subtask":
                    subtask = gson.fromJson(element.getAsJsonObject(), Subtask.class);
                    if (subtask.getStartTime() == null) {
                        subtask.setStartTime(LocalDateTime.now());
                    }
                    if (subtask.getDuration() == null) {
                        subtask.setDuration(Duration.ZERO);
                    }
                    subtasks.put(subtask.getId(), subtask);
                    addTaskToPrioritizedList(subtask);
                    break;

                case "epic":
                    epic = gson.fromJson(element.getAsJsonObject(), EpicTask.class);
                    if (epic.getStartTime() == null) {
                        epic.setStartTime(LocalDateTime.now());
                    }
                    if (epic.getDuration() == null) {
                        epic.setDuration(Duration.ZERO);
                    }
                    epics.put(epic.getId(), epic);
                    addTaskToPrioritizedList(epic);
                    break;

                default:
                    System.out.println("Не удалось загрузить задачи");
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

    // Метод для получения всех задач
    public List<Task> getAllTasks() {
        return tasks.values().stream().collect(Collectors.toList());
    }

    // Метод для удаления задачи по идентификатору
    public boolean removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            return true;
        }
        return false;
    }

    // Метод для удаления всех задач
    public void removeAllTasks() {
        tasks.clear();
    }

    // Метод для обновления описания задачи
    public void updateTaskDescription(int id, String newDescription) {
        Task task = tasks.get(id);
        if (task != null) {
            task.setDescription(newDescription);
        }
    }

    // Метод для обновления названия задачи
    public void updateTaskName(int id, String newHeading) {
        Task task = tasks.get(id);
        if (task != null) {
            task.setHeading(newHeading);
        }
    }
}

