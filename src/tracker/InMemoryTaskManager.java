package tracker;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, EpicTask> epicTasks;
    protected final HashMap<Integer, ArrayList<Subtask>> subTasks;
    protected final Map<String, Integer> taskIndexMap = new HashMap<>();
    HistoryManager historyManager = Managers.getDefaultHistory();

    int id = 0;
    String type = "";

    public InMemoryTaskManager(HashMap<Integer, Task> tasks, HashMap<Integer, EpicTask> epicTasks, HashMap<Integer, ArrayList<Subtask>> subTasks) {
        this.tasks = tasks;
        this.epicTasks = epicTasks;
        this.subTasks = subTasks;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public List<Subtask> getAllSubTasks() {
        return subTasks.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


    @Override
    public void removeAllTasks() {
        tasks.clear();
        taskIndexMap.clear();
        historyManager.clear();
    }

    @Override
    public void removeAllEpicTasks() {
        HashSet<Integer> epicIds = new HashSet<>(epicTasks.keySet());

        for (Integer epicId : epicIds) {
            subTasks.remove(epicId);
        }

        epicTasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        subTasks.clear();

        for (EpicTask epic : epicTasks.values()) {
            if (epic.getSubtasks().isEmpty()) {
                epic.setStatus(Status.NEW);
            }
        }
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        historyManager.add(epicTasks.get(id));
        return epicTasks.get(id);
    }

    @Override
    public Subtask getSubTaskById(int id) {
        return subTasks.values().stream()
                .flatMap(List::stream)
                .filter(subtask -> subtask.getId() == id)
                .peek(historyManager::add)
                .findFirst()
                .orElse(null);
    }


    public int getTaskIndex(String heading, String description, String type) {
        String taskKey = heading + " " + description + " " + type;
        if (taskIndexMap.containsKey(taskKey)) {
            return taskIndexMap.get(taskKey);
        } else {
            return -1;
        }
    }

    @Override
    public Task createTask(Task task) {
        boolean isOverlapping = tasks.values().stream()
                .filter(existingTask -> existingTask.getStartTime() != null)
                .anyMatch(existingTask -> task.isOverlapping(existingTask));

        if (isOverlapping) {
            throw new IllegalArgumentException("Задача пересекается с существующей задачей.");
        }

        type = "task";
        task.setId(++id);
        tasks.put(id, task);
        taskIndexMap.put(task.heading + " " + task.description + " " + type, id);
        return task;
    }


    @Override
    public EpicTask createEpicTask(EpicTask epicTask) {
        boolean isOverlapping = epicTasks.values().stream()
                .filter(existingEpic -> existingEpic.getStartTime() != null)
                .anyMatch(existingEpic -> epicTask.isOverlapping(existingEpic));

        if (isOverlapping) {
            throw new IllegalArgumentException("Эпическая задача пересекается с существующей эпической задачей.");
        }

        type = "epic task";
        epicTask.setId(++id);
        epicTasks.put(id, epicTask);
        taskIndexMap.put(epicTask.heading + " " + epicTask.description + " " + type, id);
        return epicTask;
    }


    @Override
    public Subtask createSubTask(Subtask subtask) {
        int epicId = subtask.epicId;

        if (!epicTasks.containsKey(epicId)) {
            return null;
        }

        if (!subTasks.containsKey(epicId)) {
            subTasks.put(epicId, new ArrayList<>());
        }

        boolean isOverlapping = subTasks.get(epicId).stream()
                .filter(existingSubtask -> existingSubtask.getStartTime() != null)
                .anyMatch(existingSubtask -> subtask.isOverlapping(existingSubtask));

        if (isOverlapping) {
            throw new IllegalArgumentException("Подзадача пересекается с существующей подзадачей.");
        }

        type = "subtask";
        subtask.setId(++id);
        subTasks.get(epicId).add(subtask);
        epicTasks.get(epicId).addSubtask(subtask);
        taskIndexMap.put(subtask.heading + " " + subtask.description + " " + type, id);
        return subtask;
    }


    @Override
    public void updateTask(int id, int comm, String change) {
        if (tasks.containsKey(id)) {
            if (comm == 1) {
                tasks.get(id).setHeading(change);
            } else if (comm == 2) {
                tasks.get(id).setDescription(change);
            }
        } else if (epicTasks.containsKey(id)) {
            if (comm == 1) {
                epicTasks.get(id).setHeading(change);
            } else if (comm == 2) {
                epicTasks.get(id).setDescription(change);
            }
        }
    }

    public List<Subtask> getSubtasks(int id) {
        return epicTasks.containsKey(id) ?
                epicTasks.get(id).getSubtasks() :
                Collections.emptyList();
    }


    @Override
    public boolean removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            String taskKey = tasks.get(id).heading + " " + tasks.get(id).description;
            taskIndexMap.remove(taskKey);
            tasks.remove(id);
            historyManager.remove(id);
            return true;
        } else if (epicTasks.containsKey(id)) {
            String taskKey = epicTasks.get(id).heading + " " + epicTasks.get(id).description;
            taskIndexMap.remove(taskKey);
            epicTasks.remove(id);
            if (!subTasks.isEmpty() && subTasks.containsKey(id)) {
                for (Subtask subtask : subTasks.get(id)) {
                    historyManager.remove(subtask.getId());
                }
            }
            subTasks.remove(id);
            historyManager.remove(id);
            return true;
        } else if (!subTasks.isEmpty()) {
            for (List<Subtask> subtasks : subTasks.values()) {
                for (Subtask subtask : subtasks) {
                    if (subtask.getId() == id) {
                        subtasks.remove(subtask);
                        historyManager.remove(id);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Status checkStatus(int id, int status) {
        if (tasks.containsKey(id)) {
            tasks.get(id).setStatus(status == 1 ? Status.IN_PROGRESS : Status.DONE);
            return tasks.get(id).status;
        }

        if (epicTasks.containsKey(id)) {
            EpicTask epicTask = epicTasks.get(id);
            if (epicTask.getSubtasks().isEmpty()) {
                epicTask.setStatus(Status.NEW);
                return epicTask.status;
            }

            boolean flag = false;
            int newCount = 0;
            int doneCount = 0;
            for (Subtask subtask : epicTask.getSubtasks()) {
                if (subtask.status == Status.IN_PROGRESS) {
                    flag = true;
                    break;
                } else if (subtask.status == Status.NEW) {
                    newCount++;
                } else if (subtask.status == Status.DONE) {
                    doneCount++;
                }
            }

            if (flag) {
                epicTask.setStatus(Status.IN_PROGRESS);
            } else if (newCount == epicTask.getSubtasks().size()) {
                epicTask.setStatus(Status.NEW);
            } else if (doneCount == epicTask.getSubtasks().size()) {
                epicTask.setStatus(Status.DONE);
            } else {
                epicTask.setStatus(Status.IN_PROGRESS);
            }
            return epicTask.status;
        }

        for (List<Subtask> subtaskList : subTasks.values()) {
            for (Subtask subtask : subtaskList) {
                if (subtask.getId() == id) {
                    subtask.setStatus(status == 1 ? Status.IN_PROGRESS : Status.DONE);
                    return subtask.status;
                }
            }
        }

        return null;
    }

    public boolean checkTaskId(int id) {
        if (tasks.containsKey(id)) {
            return true;
        }
        return false;
    }

    public boolean checkEpicTaskId(int id) {
        if (epicTasks.containsKey(id)) {
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    public TreeSet<Task> getPrioritizedTasks() {
        TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator
                .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Task::getId));

        for (Task task : tasks.values()) {
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }

        for (EpicTask epic : epicTasks.values()) {
            if (epic.getStartTime() != null) {
                prioritizedTasks.add(epic);

                for (Subtask subtask : epic.getSubtasks()) {
                    if (subtask.getStartTime() != null) {
                        prioritizedTasks.add(subtask);
                    } else {
                        System.out.println("Подзадача с ID " + subtask.getId() + " имеет null startTime и не будет добавлена.");
                    }
                }
            }
        }

        return prioritizedTasks;
    }
}