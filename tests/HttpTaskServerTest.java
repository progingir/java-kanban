import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import tracker.*;
import tracker.Managers;
import tracker.Manager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private Manager manager;
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private final Task task = new Task(1, "Task 1", "Groceries", 2, Instant.now().minusMillis(100), Status.NEW);
    private final Task task2 = new Task(2, "Task 2", "Sport", 0,
            Instant.ofEpochMilli(1686603600000L), Status.IN_PROGRESS);
    private final Epic epic2 = new Epic(4, "Epic 3", "Household chores", 19,
            Instant.ofEpochMilli(1686085200000L), Status.DONE);
    private final Epic epic = new Epic(3, "Epic 1", "Shopping", 11,
            Instant.now().minusMillis(100), Status.DONE);
    private final Subtask subtask = new Subtask(3, "Subtask 1", "Buy milk", 14,
            Instant.ofEpochMilli(1687208400000L), 19, Status.IN_PROGRESS);
    private final Subtask subtask2 = new Subtask(4, "Subtask 2", "Clean the kitchen", 20,
            Instant.ofEpochMilli(1686430800000L), 12, Status.DONE);
    private static final Gson gson = new Gson();
    private final HttpClient client = HttpClient.newHttpClient();
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

    @BeforeEach
    public void startServers() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        manager = Managers.getDefault("http://localhost:8078");
        httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }

    @Test
    void shouldPOSTTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(task.toString(), manager.getTaskById(task.getId()).toString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void shouldGETTask() throws IOException, InterruptedException {
        manager.addTask(task);
        manager.addTask(task2);
        Map<Integer, Task> testList = Map.of(task.getId(), task, task2.getId(),task2);
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        HashMap<Integer, Task> tasksFromJson =
                gson.fromJson(jsonElement, new TypeToken<HashMap<Integer, Task>>(){}.getType());

        assertEquals(200, response.statusCode());
        assertTrue(tasksFromJson.size() == 2);
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        int id = task.getId();
        manager.addTask(task);
        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int jsonId = jsonObject.get("id").getAsInt();
        String jsonDescription = jsonObject.get("description").getAsString();

        assertEquals(id, jsonId);
        assertTrue(jsonElement.isJsonObject(), "Incorrect JSON");
        assertEquals(task.getDescription(), jsonDescription);
    }

    @Test
    void shouldDELETETask() throws IOException, InterruptedException {
        manager.addTask(task);
        manager.addTask(task2);
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());
        assertEquals("All tasks were removed", response.body());
    }

    @Test
    void shouldPOSTEpic() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(epic.toString(), manager.getEpicById(epic.getId()).toString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void shouldGETEpic() throws IOException, InterruptedException {
        manager.addEpic(epic);
        manager.addEpic(epic2);
        Map<Integer, Epic> testList = Map.of(epic.getId(), epic, epic2.getId(), epic2);
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        HashMap<Integer, Task> tasksFromJson =
                gson.fromJson(jsonElement, new TypeToken<HashMap<Integer, Epic>>(){}.getType());

        assertEquals(200, response.statusCode());
        assertTrue(tasksFromJson.size() == 2);
    }

    @Test
    void shouldDELETEEpic() throws IOException, InterruptedException {
        manager.addEpic(epic);
        manager.addEpic(epic2);
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());
        assertEquals("All epics were removed", response.body());
    }

    @Test
    void shouldPOSTSubtask() throws IOException, InterruptedException {
        manager.addEpic(epic);
        manager.addSubtask(subtask);
        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        String json = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(subtask.toString(), manager.getSubtaskById(subtask.getId()).toString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void shouldGETSubtask() throws IOException, InterruptedException {
        manager.addEpic(epic);
        manager.addSubtask(subtask);
        Map<Integer, Subtask> testList = Map.of(subtask.getId(), subtask);
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        HashMap<Integer, Subtask> tasksFromJson =
                gson.fromJson(jsonElement, new TypeToken<HashMap<Integer, Subtask>>(){}.getType());

        assertEquals(200, response.statusCode());
        assertTrue(tasksFromJson.size() == 1);
    }

    @Test
    void shouldDELETESubtask() throws IOException, InterruptedException {
        manager.addSubtask(subtask);
        manager.addSubtask(subtask2);
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());
        assertEquals("All subtasks were removed", response.body());
    }

    @Test
    void shouldGETHistory() throws IOException, InterruptedException {
        manager.addTask(task);
        manager.addTask(task2);
        manager.getTaskById(task.getId());
        manager.getTaskById(task2.getId());
        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertEquals(200, response.statusCode());
        assertTrue(jsonElement.isJsonArray(), "Incorrect JSON");
        assertEquals(2, jsonArray.size());
    }

    @Test
    void shouldGETPrioritized() throws IOException, InterruptedException {
        manager.addTask(task);
        manager.addTask(task2);
        task.setStartTime(Instant.ofEpochMilli(1687003200000L));
        task2.setStartTime(Instant.ofEpochMilli(1687006800000L));
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());

        assertEquals(200, response.statusCode());
        assertTrue(jsonElement.isJsonArray(), "Incorrect JSON");
        assertEquals(2, jsonElement.getAsJsonArray().size());
    }

    @AfterEach
    public void afterEach() {
        httpTaskServer.stop();
        kvServer.stop();
    }
}
