package tracker;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class InMemoryTaskManager implements Manager {
    protected int id;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, EpicTask> epics = new HashMap<>();
    protected HistoryManager historyManager;
    private final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime);
    private final Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    public InMemoryTaskManager() {
        id = 0;
        historyManager = Managers.getDefaultHistory();
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private int idIncrease() {
        return ++id;
    }

    @Override
    public Task addTask(Task task) {
        if (task != null && !tasks.containsKey(task.getId())) {
            if (task.getStartTime() == null || task.getDuration() == null) {
                throw new IllegalArgumentException("Время начала задачи и продолжительность не могут быть null");
            }
            int newId = task.getId();
            if (newId == 0) {
                newId = idIncrease();
                task.setId(newId);
            }
            tasks.put(newId, task);
            addTaskToPrioritizedList(task);
        } else {
            return null;
        }
        return task;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null && tasks.containsKey(id)) {
            historyManager.add(task);
        } else {
            return null;
        }
        return task;
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        if (tasks.size() != 0) {
            return tasks;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            addTaskToPrioritizedList(task);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.removeIf(task -> task.getId() == id);
        }
    }

    @Override
    public EpicTask addEpic(EpicTask epic) {
        if (epic != null && !epics.containsKey(epic.getId())) {
            int newId = epic.getId();
            if (newId == 0) {
                newId = idIncrease();
                epic.setId(newId);
            }
            epics.put(newId, epic);
        } else {
            return null;
        }
        return epic;
    }

    @Override
    public EpicTask getEpicById(int id) {
        EpicTask epic = epics.get(id);
        if (epic != null && epics.containsKey(id)) {
            historyManager.add(epic);
        } else {
            return null;
        }
        return epic;
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public HashMap<Integer, EpicTask> getEpics() {
        if (epics.size() != 0) {
            return epics;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public void updateEpic(EpicTask epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            statusUpdate(epic);
            updateEpicTime(epic);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            EpicTask epic = epics.get(id);
            for (Integer subtaskId : epic.getSubtasksIds()) {
                historyManager.remove(id);
                subtasks.remove(subtaskId);
                prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), subtaskId));
            }
            epics.remove(id);
            historyManager.remove(epic.getId());
        }
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        if (subtask != null && !subtasks.containsKey(subtask.getId())) {
            int newId = subtask.getId();
            if (newId == 0) {
                newId = idIncrease();
                subtask.setId(newId);
            }
            EpicTask epic = epics.get(subtask.epicId);
            if (epic != null) {
                addTaskToPrioritizedList(subtask);
                subtasks.put(newId, subtask);
                epic.setSubtasksIds(newId);
                statusUpdate(epic);
                updateEpicTime(epic);
            } else {
                throw new IllegalArgumentException("Эпическая задача с id " + subtask.epicId + " не существует.");
            }
        } else {
            return null;
        }
        return subtask;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null && subtasks.containsKey(id)) {
            historyManager.add(subtask);
        } else {
            return null;
        }
        return subtask;
    }

    @Override
    public void deleteSubtasks() {
        for (EpicTask epic : epics.values()) {
            for (Integer subtaskId : epic.getSubtasksIds()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
                Subtask subtask = subtasks.get(subtaskId);
                prioritizedTasks.remove(subtask);
            }
            epic.getSubtasksIds().clear();
        }
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        if (subtasks.size() != 0) {
            return subtasks;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            EpicTask epic = getEpicById(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            statusUpdate(epic);
            addTaskToPrioritizedList(subtask);
            updateEpicTime(epic);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.remove(id);
            EpicTask epic = epics.get(subtask.epicId);
            subtasks.remove(id);
            epic.getSubtasksIds().remove(id);
            historyManager.remove(id);
            statusUpdate(epic);
            updateEpicTime(epic);
            prioritizedTasks.remove(subtask);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int id) {
        if (epics.containsKey(id)) {
            EpicTask epic = epics.get(id);
            List<Subtask> newSubtasks = new ArrayList<>();
            for (int i = 0; i < epic.getSubtasksIds().size(); i++) {
                newSubtasks.add(subtasks.get(epic.getSubtasksIds().get(i)));
            }
            return newSubtasks;
        } else {
            return new ArrayList<>();
        }
    }

    private void statusUpdate(EpicTask epic) {
        boolean isNew = true;
        boolean isDone = true;

        if (epic.getSubtasksIds().size() == 0) {
            epic.setStatus(Status.NEW);
            return;
        }

        for (Integer epicSubtask : epic.getSubtasksIds()) {
            Status status = subtasks.get(epicSubtask).getStatus();
            if (status != Status.NEW) {
                isNew = false;
            }
            if (status != Status.DONE) {
                isDone = false;
            }
        }

        if (isNew) {
            epic.setStatus(Status.NEW);
        } else if (isDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void updateEpicTime(EpicTask epic) {
        List<Subtask> subtasks = getSubtasksOfEpic(epic.getId());

        if (subtasks.isEmpty()) {
            // Если подзадач нет, устанавливаем время начала и окончания в null
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }

        LocalDateTime startTime = subtasks.get(0).getStartTime();
        LocalDateTime endTime = subtasks.get(0).getEndTime();

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
        }

        epic.setStartTime(startTime);

        // Вычисляем продолжительность
        long durationMillis = Duration.between(startTime, endTime).toMillis();
        epic.setDuration(Duration.ofMillis(durationMillis));
    }

    public void addTaskToPrioritizedList(Task task) {
        if (task.getStartTime() == null) {
            throw new IllegalArgumentException("Время начала задачи не может быть null");
        }
        boolean isValidated = validation(task);
        if (!isValidated) {
            prioritizedTasks.add(task);
        } else {
            throw new TaskConflictException("Существует проблема, вызванная временем схожих задач");
        }
    }

    private boolean validation(Task task) {
        if (task.getStartTime() == null || task.getDuration() == null) {
            return false; // Нельзя валидировать задачу без времени начала или продолжительности
        }

        boolean isOverlapping = false;
        Instant startOfTask = task.getStartTime().atZone(ZoneId.systemDefault()).toInstant();
        Instant endOfTask = startOfTask.plus(task.getDuration());

        for (Task taskValue : prioritizedTasks) {
            if (taskValue.getStartTime() == null || taskValue.getDuration() == null) {
                continue; // Пропускаем задачи без времени начала или продолжительности
            }

            Instant startTime = taskValue.getStartTime().atZone(ZoneId.systemDefault()).toInstant();
            Instant endTime = startTime.plus(taskValue.getDuration());

            boolean isCovering = startTime.isBefore(startOfTask) && endTime.isAfter(endOfTask);
            boolean isOverlappingByEnd = startTime.isBefore(startOfTask) && endTime.isAfter(startOfTask);
            boolean isOverlappingByStart = startTime.isBefore(endOfTask) && endTime.isAfter(endOfTask);
            boolean isWithin = startTime.isAfter(startOfTask) && endTime.isBefore(endOfTask);
            isOverlapping = isCovering || isOverlappingByEnd || isOverlappingByStart || isWithin;
        }
        return isOverlapping;
    }
}