package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tracker.EpicTask;
import tracker.FileBackedTaskManager;
import tracker.Managers;
import tracker.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;


class FileBackedManagerTest extends ManagerTest<FileBackedTaskManager> {
    private final Path path = Path.of("resources/back up.csv"); // Путь к файлу для сохранения задач
    private final File file = new File(String.valueOf(path));

    // Создание тестовых задач
    private final Task task = new Task("Task 1", "Groceries", 1, Duration.ofMinutes(30),
            LocalDateTime.of(2023, 6, 13, 10, 0));
    private final EpicTask epic = new EpicTask("Epic 1", "Go to the shop", 2);

    @BeforeEach
    void beforeEach() {
        // Инициализация менеджера перед каждым тестом
        manager = new FileBackedTaskManager(Managers.getDefaultHistory(), "resources/back up.csv");
    }

    @AfterEach
    void afterEach() {
        // Удаление файла после каждого теста
        try {
            Files.delete(path);
        } catch (IOException exception) {
            exception.printStackTrace(); // Исправлено: выводим стек ошибок
        }
    }
}