package tracker;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, String path) {
        super(historyManager);
        this.file = new File(path);
    }

    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public EpicTask addEpic(EpicTask epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public Task getTaskById(int id) {
        Task foundTask = super.getTaskById(id);
        if (foundTask != null) {
            save();
        }
        return foundTask;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask foundSubtask = super.getSubtaskById(id);
        if (foundSubtask != null) {
            save();
        }
        return foundSubtask;
    }

    @Override
    public EpicTask getEpicById(int id) {
        EpicTask foundEpic = super.getEpicById(id);
        if (foundEpic != null) {
            save();
        }
        return foundEpic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(EpicTask epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,title,status,description,startTime,duration,epic\n"); // Заголовок для файла
            HashMap<Integer, String> allTasks = new HashMap<>();

            HashMap<Integer, Task> tasks = super.getTasks();
            for (Integer id : tasks.keySet()) {
                allTasks.put(id, tasks.get(id).toString());
            }

            HashMap<Integer, EpicTask> epics = super.getEpics();
            for (Integer id : epics.keySet()) {
                allTasks.put(id, epics.get(id).toString());
            }

            HashMap<Integer, Subtask> subtasks = super.getSubtasks();
            for (Integer id : subtasks.keySet()) {
                allTasks.put(id, subtasks.get(id).toString());
            }

            for (String value : allTasks.values()) {
                writer.write(String.format("%s\n", value)); // Запись задач в файл
            }

            writer.write("history\n"); // Добавлено для обозначения начала истории
            for (Task task : super.getHistory()) {
                writer.write(task.getId() + ",");
            }
            writer.write("\n"); // Добавлен символ новой строки в конце истории

        } catch (IOException exception) {
            throw new ManagerSaveException("Не удалось записать файл");
        }
    }

    private static Task fromString(String content) {
        String[] elements = content.split(",");
        int id = Integer.parseInt(elements[0]);
        Type type = Type.valueOf(elements[1]);
        String title = elements[2];
        Status status = Status.valueOf(elements[3]);
        String description = elements[4];

        // Преобразуем строку в LocalDateTime
        LocalDateTime startTime = LocalDateTime.parse(elements[5]);
        // Преобразуем строку в Duration
        Duration duration = Duration.ofMillis(Long.parseLong(elements[6]));
        int epicId = (elements.length == 8) ? Integer.parseInt(elements[7]) : 0;

        switch (type) {
            case TASK:
                return new Task(title, description, id, duration, startTime);
            case SUB:
                return new Subtask(title, description, id, epicId, duration, startTime);
            case EPIC:
                return new EpicTask(title, description, id); // Учитываем, что EpicTask требует только заголовок, описание и id
            default:
                throw new IllegalArgumentException("Неизвестный тип: " + type);
        }
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> tasksIds = new ArrayList<>();
        if (value != null && !value.isEmpty()) {
            String[] idsString = value.split(",");
            for (String idString : idsString) {
                tasksIds.add(Integer.valueOf(idString));
            }
        }
        return tasksIds;
    }

    public void loadFromFile() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty() || line.contains("id")) {
                    continue; // Пропускаем пустые строки и заголовок
                }

                Task task = fromString(line);
                if (task instanceof EpicTask) {
                    super.addEpic((EpicTask) task);
                } else if (task instanceof Subtask) {
                    super.addSubtask((Subtask) task);
                } else {
                    addTask(task);
                }
            }

            // Чтение истории
            String lineWithHistory = bufferedReader.readLine();
            if (lineWithHistory != null) {
                for (int id : historyFromString(lineWithHistory)) {
                    if (super.getTasks().containsKey(id)) {
                        historyManager.add(super.getTaskById(id));
                    } else if (super.getSubtasks().containsKey(id)) {
                        historyManager.add(super.getSubtaskById(id));
                    } else if (super.getEpics().containsKey(id)) {
                        historyManager.add(super.getEpicById(id));
                    }
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Не удалось прочитать файл");
        }
    }
}