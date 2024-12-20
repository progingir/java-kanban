package test;

import tracker.HttpTaskManager;
import tracker.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.Managers;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends ManagerTest<HttpTaskManager> {
    private static KVServer server;

    // Создание тестовых задач с новыми параметрами
    private final Task task = new Task("Task 1", "Groceries", 1, Duration.ofMinutes(30),
            LocalDateTime.of(2023, 6, 13, 10, 0));
    private final EpicTask epic = new EpicTask("Epic 1", "Go to the shop", 2);
    private final Subtask subtask = new Subtask("Subtask 1", "Buy milk", 3, 2, Duration.ofMinutes(15),
            LocalDateTime.of(2023, 6, 14, 10, 0));

    @BeforeEach
    void setManager() {
        manager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
    }

    @BeforeAll
    static void startServer() throws IOException {
        server = new KVServer();
        server.start();
    }


    @AfterAll
    static void stopServer() {
        server.stop();
    }
}
