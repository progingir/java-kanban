package tracker;

import java.io.*;
import java.time.Instant;
import java.util.*;

import static tracker.Type.*;

public class FileBackedManager extends InMemoryManager {
    private final File file;
    private final InMemoryManager manager = new InMemoryManager();

    public FileBackedManager(HistoryManager historyManager, String path) {
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
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public Task getTaskById(int id) {
        Task foundTask = super.getTaskById(id);
        if (foundTask != null) {
            save();
            return foundTask;
        } else {
            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask foundSubtask = super.getSubtaskById(id);
        if (foundSubtask != null) {
            save();
            return foundSubtask;
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpicById(int id) {
        Epic foundEpic = super.getEpicById(id);
        if (foundEpic != null) {
            save();
            return foundEpic;
        } else {
            return null;
        }
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
    public void updateEpic(Epic epic) {
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
            writer.write("id,type,title,status,description,startTime,duration,epic");
            HashMap<Integer, String> allTasks = new HashMap<>();

            HashMap<Integer, Task> tasks = super.getTasks();
            for (Integer id : tasks.keySet()) {
                allTasks.put(id, tasks.get(id).toStringFromFile());
            }

            HashMap<Integer, Epic> epics = super.getEpics();
            for (Integer id : epics.keySet()) {
                allTasks.put(id, epics.get(id).toStringFromFile());
            }

            HashMap<Integer, Subtask> subtasks = super.getSubtasks();
            for (Integer id : subtasks.keySet()) {
                allTasks.put(id, subtasks.get(id).toStringFromFile());
            }

            for (String value : allTasks.values()) {
                 writer.write(String.format("%s\n", value));
            }
            writer.write("\n");

            for (Task task : super.getHistory()) {
                writer.write(task.getId() + ",");
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Unable to write file");
        }
    }

    private static Task fromString(String content) {
        Task task = new Task();
        List<Integer> listOfSubtasksIds = new ArrayList<>();
        int id = 0;
        Type type = null;
        String title = null;
        Status status = null;
        String description = null;
        Instant startTime = null;
        long duration = 0;
        int epicId = 0;
        String[] elements = content.split(",");
        if (elements[2].equals("EPIC")) {
            listOfSubtasksIds = List.of(Integer.parseInt(elements[0]));
        } else {
            id = Integer.parseInt(elements[0]);
            type = Type.valueOf(elements[1]);
            title = String.valueOf(elements[2]);
            status = Status.valueOf(elements[3]);
            description = elements[4];
            startTime = Instant.parse(elements[5]);
            duration = Long.parseLong(elements[6]);
            if (elements.length == 8) {
                epicId = Integer.parseInt(elements[7]);
            }
        }

        if (type == TASK) {
            return new Task(id, title, description, duration, startTime, status);
        } else if (type == SUBTASK) {
            return new Subtask(epicId, title, description, duration, startTime, id, status);
        } else if (type == EPIC) {
            return new Epic((ArrayList<Integer>) listOfSubtasksIds, id, title, description, duration, startTime, status);
        }
        return task;
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> tasksIds = new ArrayList<>();
        if (value != null) {
            String[] idsString = value.split(",");
            for (String idString : idsString) {
                tasksIds.add(Integer.valueOf(idString));
            }
        }
        return tasksIds;
    }

    public void loadFromFile() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader((file)))) {

            String line;
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (line.equals("")) {
                    break;
                }
                if (line.contains("id")) {
                    continue;
                }

                Task task = fromString(line);

                if (task.getType().equals(EPIC)) {
                    super.addEpic((Epic) task);
                } else if (task.getType().equals(SUBTASK)) {
                    super.addSubtask((Subtask) task);
                } else {
                    addTask(task);
                }
            }

            String lineWithHistory = bufferedReader.readLine();
            for (int id : historyFromString(lineWithHistory)) {
                if (manager.tasks.containsKey(id)) {
                    historyManager.add(tasks.get(id));
                } else if (manager.subtasks.containsKey(id)) {
                    historyManager.add(subtasks.get(id));
                } else {
                    historyManager.add(epics.get(id));
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Unable to read file");
        }

    }
}