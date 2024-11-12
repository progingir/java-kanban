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
    private final String filePath; // путь к файлу для автосохранения

    public FileBackedTaskManager(HashMap<Integer, Task> tasks, HashMap<Integer, EpicTask> epicTasks, HashMap<Integer, ArrayList<Subtask>> subTasks, String filePath) {
        super(tasks, epicTasks, subTasks);
        this.filePath = filePath;
    }


    public void load() {
        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));
            for (String line : lines.subList(0, lines.size())) { // пропускаем заголовок
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

    // статический метод для загрузки данных из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(new HashMap<>(), new HashMap<>(), new HashMap<>(), file.getPath());
        // загружаем данные из файла
        manager.load();
        return manager;
    }

    // метод для сохранения состояния в файл
    public void save() {
        StringBuilder csvData = new StringBuilder();
        csvData.append("id,type,name,status,description,epic\n");

        // сохранение задач
        for (Task task : super.getAllTasks()) {
            csvData.append(task.toString()).append("\n");
        }

        // сохранение эпиков
        for (EpicTask epic : super.getAllEpicTasks()) {
            csvData.append(epic.toString()).append("\n");
        }

        // сохранение подзадач
        for (Subtask subtask : super.getAllSubTasks()) {
            csvData.append(subtask.toString()).append("\n");
        }

        // запись в файл
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(csvData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public EpicTask createEpicTask(EpicTask epicTask) {
        EpicTask createdEpic = super.createEpicTask(epicTask);
        save();
        return createdEpic;
    }

    @Override
    public Subtask createSubTask(Subtask subtask) {
        Subtask createdSubtask = super.createSubTask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public boolean removeTaskById(int id) {
        boolean removed = super.removeTaskById(id);
        if (removed) {
            save();
        }
        return removed;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpicTasks() {
        super.removeAllEpicTasks();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void updateTask(int id, int comm, String change) {
        super.updateTask(id, comm, change);
        save();
    }
}