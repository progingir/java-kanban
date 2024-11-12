package tracker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String filePath; // Путь к файлу для автосохранения

    public FileBackedTaskManager(HashMap<Integer, Task> tasks, HashMap<Integer, EpicTask> epicTasks, HashMap<Integer, ArrayList<Subtask>> subTasks, String filePath) {
        super(tasks, epicTasks, subTasks);
        this.filePath = filePath;
    }


    public void load() {
        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));
            for (String line : lines.subList(0, lines.size())) { // Пропускаем заголовок
                String[] parts = line.split(",");
                switch (parts[1]) {
                    case "TASK":
                        Task task = Task.fromString(line);
                        super.createTask(task);
                        break;
                    case "EPIC":
                        EpicTask epic = EpicTask.fromString(line);
                        super.createEpicTask(epic);
                        break;
                    case "SUBTASK":
                        Subtask subtask = Subtask.fromString(line);
                        super.createSubTask(subtask);
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Статический метод для загрузки данных из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        // Создаем новый экземпляр FileBackedTaskManager с пустыми коллекциями
        FileBackedTaskManager manager = new FileBackedTaskManager(new HashMap<>(), new HashMap<>(), new HashMap<>(), file.getPath());
        // Загружаем данные из файла
        manager.load();
        return manager;
    }

    // Метод для сохранения состояния в файл
    public void save() {
        StringBuilder csvData = new StringBuilder();
        csvData.append("id,type,name,status,description,epic\n");

        // Сохранение задач
        for (Task task : super.getAllTasks()) {
            csvData.append(task.toString()).append("\n");
        }

        // Сохранение эпиков
        for (EpicTask epic : super.getAllEpicTasks()) {
            csvData.append(epic.toString()).append("\n");
        }

        // Сохранение подзадач
        for (Subtask subtask : super.getAllSubTasks()) {
            csvData.append(subtask.toString()).append("\n");
        }

        // Запись в файл
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(csvData.toString());
            System.out.println("Данные сохранены в файл: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save(); // Сохранение после создания задачи
        return createdTask;
    }

    @Override
    public EpicTask createEpicTask(EpicTask epicTask) {
        EpicTask createdEpic = super.createEpicTask(epicTask);
        save(); // Сохранение после создания эпика
        return createdEpic;
    }

    @Override
    public Subtask createSubTask(Subtask subtask) {
        Subtask createdSubtask = super.createSubTask(subtask);
        save(); // Сохранение после создания подзадачи
        return createdSubtask;
    }

    @Override
    public boolean removeTaskById(int id) {
        boolean removed = super.removeTaskById(id);
        if (removed) {
            save(); // Сохранение после удаления задачи
        }
        return removed;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save(); // Сохранение после удаления всех задач
    }

    @Override
    public void removeAllEpicTasks() {
        super.removeAllEpicTasks();
        save(); // Сохранение после удаления всех эпиков
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save(); // Сохранение после удаления всех подзадач
    }

    @Override
    public void updateTask(int id, int comm, String change) {
        super.updateTask(id, comm, change);
        save(); // Сохранение после обновления задачи
    }
}