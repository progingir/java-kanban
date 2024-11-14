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
    private final String filePath;
    private int lastId;

    public FileBackedTaskManager(HashMap<Integer, Task> tasks, HashMap<Integer, EpicTask> epicTasks, HashMap<Integer, ArrayList<Subtask>> subTasks, String filePath) {
        super(tasks, epicTasks, subTasks);
        this.filePath = filePath;
        this.lastId = 0;
    }

    public void load() {
        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));
            for (String line : lines.subList(1, lines.size())) {
                String[] parts = line.split(",");
                switch (parts[1]) {
                    case "TASK":
                        Task task = Task.fromString(line);
                        if (!tasks.containsKey(task.getId())) {
                            tasks.put(task.getId(), task);
                            lastId = Math.max(lastId, task.getId());
                        }
                        break;
                    case "EPIC":
                        EpicTask epic = EpicTask.fromString(line);
                        if (!epicTasks.containsKey(epic.getId())) {
                            epicTasks.put(epic.getId(), epic);
                            lastId = Math.max(lastId, epic.getId());
                        }
                        break;
                    case "SUB":
                        Subtask subtask = Subtask.fromString(line);
                        if (!subTasks.containsKey(subtask.epicId)) {
                            subTasks.put(subtask.epicId, new ArrayList<>());
                        }
                        ArrayList<Subtask> epicSubtasks = subTasks.get(subtask.epicId);
                        if (!epicSubtasks.contains(subtask)) {
                            epicSubtasks.add(subtask);
                            lastId = Math.max(lastId, subtask.getId());
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Неизвестный тип задачи: " + parts[1]);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла: " + filePath, e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(new HashMap<>(), new HashMap<>(), new HashMap<>(), file.getPath());
        manager.load();
        return manager;
    }

    private void save() {
        StringBuilder csvData = new StringBuilder();
        csvData.append("id,type,name,status,description,epic\n");

        for (Task task : super.getAllTasks()) {
            csvData.append(task.toString()).append("\n");
        }

        for (EpicTask epic : super.getAllEpicTasks()) {
            csvData.append(epic.toString()).append("\n");
        }

        for (Subtask subtask : super.getAllSubTasks()) {
            csvData.append(subtask.toString()).append("\n");
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(csvData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Task createTask(Task task) {
        if (lastId == 0) {
            lastId = 1;
        } else {
            lastId++;
        }
        Task createdTask = super.createTask(task);
        createdTask.setId(lastId);
        save();
        return createdTask;
    }

    @Override
    public EpicTask createEpicTask(EpicTask epicTask) {
        if (lastId == 0) {
            lastId = 1;
        } else {
            lastId++;
        }
        EpicTask createdEpic = super.createEpicTask(epicTask);
        createdEpic.setId(lastId);
        save();
        return createdEpic;
    }

    @Override
    public Subtask createSubTask(Subtask subtask) {
        if (lastId == 0) {
            lastId = 1;
        } else {
            lastId++;
        }
        Subtask createdSubtask = super.createSubTask(subtask);
        createdSubtask.setId(lastId);
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
        lastId = 0;
        save();
    }

    @Override
    public void removeAllEpicTasks() {
        super.removeAllEpicTasks();
        lastId = 0;
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        lastId = 0;
        save();
    }

    @Override
    public void updateTask(int id, int comm, String change) {
        super.updateTask(id, comm, change);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        String taskData = getTaskByIdFromFile(id);
        if (taskData.startsWith("Задача с ID")) {
            System.out.println(taskData);
            return null;
        }
        Task task = Task.fromString(taskData);
        historyManager.add(task);
        return task;
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        String epicData = getTaskByIdFromFile(id);
        if (epicData.startsWith("Задача с ID")) {
            System.out.println(epicData);
            return null;
        }
        EpicTask epic = EpicTask.fromString(epicData);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubTaskById(int id) {
        String subtaskData = getTaskByIdFromFile(id);
        if (subtaskData.startsWith("Задача с ID")) {
            System.out.println(subtaskData);
            return null;
        }
        Subtask subtask = Subtask.fromString(subtaskData);
        historyManager.add(subtask);
        return subtask;
    }

    public String getTaskByIdFromFile(int id) {
        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));
            for (String line : lines.subList(1, lines.size())) {
                String[] parts = line.split(",");
                int taskId = Integer.parseInt(parts[0]);
                if (taskId == id) {
                    return line;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла: " + filePath, e);
        } catch (NumberFormatException e) {
            return "Ошибка формата ID задачи в строке: " + e.getMessage();
        }

        return "Задача с ID " + id + " не найдена.";
    }
}