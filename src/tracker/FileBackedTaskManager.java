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
            for (String line : lines.subList(1, lines.size())) {
                String[] parts = line.split(",");
                switch (parts[1]) {
                    case "TASK":
                        Task task = Task.fromString(line);
                        if (!tasks.containsKey(task.getId())) {
                            tasks.put(task.getId(), task);
                        }
                        break;
                    case "EPIC":
                        EpicTask epic = EpicTask.fromString(line);
                        if (!epicTasks.containsKey(epic.getId())) {
                            epicTasks.put(epic.getId(), epic);
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



    // статический метод для загрузки данных из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(new HashMap<>(), new HashMap<>(), new HashMap<>(), file.getPath());
        // загружаем данные из файла
        manager.load();
        return manager;
    }

    // метод для сохранения состояния в файл
    private void save() {
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

    @Override
    public Task getTaskById(int id) {
        String taskData = getTaskByIdFromFile(id);
        if (taskData.startsWith("Задача с ID")) {
            System.out.println(taskData); // Выводим сообщение, если задача не найдена
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
            System.out.println(epicData); // Выводим сообщение, если эпик не найден
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
            System.out.println(subtaskData); // Выводим сообщение, если подзадача не найдена
            return null;
        }
        Subtask subtask = Subtask.fromString(subtaskData);
        historyManager.add(subtask);
        return subtask;
    }

    public String getTaskByIdFromFile(int id) {
        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));
            for (String line : lines.subList(1, lines.size())) { // пропускаем заголовок
                String[] parts = line.split(",");
                int taskId = Integer.parseInt(parts[0]);
                if (taskId == id) {
                    return line; // Возвращаем строку, представляющую задачу
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


// сделать 1) метод удаления тоже из файла 2) по-другому добавлять эпик, чтобы сохранялись подзадачи 3) сделать сохранение в файле по айдишникам