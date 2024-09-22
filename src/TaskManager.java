import java.util.*;
/*
я убрала все строковые выводы в этом классе (потому что у нас не консольное приложение :), )
но добавила их в main просто для удобства проверки
А так поправила все, что нужно было (даже статус у эпиков работает как надо, представляете?)
Вам хорошей проверки, а мне - сил для следующих правок
*/

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HashMap<Integer, ArrayList<Subtask>> subTasks;
    private final Map<String, Integer> taskIndexMap = new HashMap<>();

    int id = 0;

    public TaskManager(HashMap<Integer, Task> tasks,
                       HashMap<Integer, EpicTask> epicTasks,
                       HashMap<Integer, ArrayList<Subtask>> subTasks) {
        this.tasks = tasks;
        this.epicTasks = epicTasks;
        this.subTasks = subTasks;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public List<Subtask> getAllSubTasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        for (List<Subtask> subtasksForEpic : subTasks.values()) {
            allSubtasks.addAll(subtasksForEpic);
        }
        return allSubtasks;
    }

    public void removeAllTasks() {
        tasks.clear();
        taskIndexMap.clear();
    }

    public void removeAllEpicTasks() {
        HashSet<Integer> epicIds = new HashSet<>(epicTasks.keySet());

        for (Integer epicId : epicIds) {
            subTasks.remove(epicId);
        }

        epicTasks.clear();
    }

    public void removeAllSubTasks() {
        subTasks.clear();

        for (EpicTask epic : epicTasks.values()) {
            if (epic.getSubtasks().isEmpty()) {
                epic.setStatus(Status.NEW);
            }
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public EpicTask getEpicTaskById(int id) {
        return epicTasks.get(id);
    }

    public Subtask getSubTaskById(int id) {
        for (List<Subtask> subtasks : subTasks.values()) {
            for (Subtask subtask : subtasks) {
                if (subtask.getId() == id) {
                    return subtask;
                }
            }
        }
        return null;
    }

    public int getTaskIndex(String heading, String description) {
        String taskKey = heading + " " + description;
        if (taskIndexMap.containsKey(taskKey)) {
            return taskIndexMap.get(taskKey);
        } else {
            return -1;
        }
    }

    public Task createTask(Task task) {
        task.setId(++id);
        tasks.put(id, task);
        taskIndexMap.put(task.heading + " " + task.description, id);
        return task;
    }

    public EpicTask createEpicTask(EpicTask epicTask) {
        epicTask.setId(++id);
        epicTasks.put(id, epicTask);
        taskIndexMap.put(epicTask.heading + " " + epicTask.description, id);
        return epicTask;
    }

    public Subtask createSubTask(Subtask subtask) {
        int index = getTaskIndex(subtask.heading, subtask.description);
        if (index != -1) {
            for (Subtask existingSubtask : subTasks.getOrDefault(subtask.getEpicTask(epicTasks).id,
                    new ArrayList<>())) {
                if (existingSubtask.getId() == index) {
                    return existingSubtask;
                }
            }
        } else {
            subtask.setId(++id);
            if (subTasks.containsKey(subtask.getEpicTask(epicTasks).id)) {
                subTasks.get(subtask.getEpicTask(epicTasks).id).add(subtask);
            } else {
                ArrayList<Subtask> subtaskArray = new ArrayList<>();
                subtaskArray.add(subtask);
                subTasks.put(subtask.getEpicTask(epicTasks).id, subtaskArray);
            }
            taskIndexMap.put(subtask.heading + " " + subtask.description, id);
            return subtask;
        }
        return null;
    }

    public void updateTask(int id, int comm, String change) {
        if (comm == 1) {
            tasks.get(id).setHeading(change);
        } else if (comm == 2) {
            tasks.get(id).setDescription(change);
        }
    }

    public List<Subtask> getSubtasks(int id) {
        if (epicTasks.containsKey(id)) {
            EpicTask epic = epicTasks.get(id);
            return epic.getSubtasks();
        } else {
            return new ArrayList<>();
        }
    }

    public boolean removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            String taskKey = tasks.get(id).heading + " " + tasks.get(id).description;
            taskIndexMap.remove(taskKey);
            tasks.remove(id);
            return true;
        } else if (epicTasks.containsKey(id)) {
            String taskKey = epicTasks.get(id).heading + " " + epicTasks.get(id).description;
            taskIndexMap.remove(taskKey);
            epicTasks.remove(id);
            subTasks.remove(id);
            return true;
        } else if (!subTasks.isEmpty()) {
            for (List<Subtask> subtasks : subTasks.values()) {
                for (Subtask subtask : subtasks) {
                    if (subtask.getId() == id) {
                        subtasks.remove(subtask);
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
                } else if(subtask.status == Status.DONE){
                    doneCount ++;
                }
            }

            if (flag) {
                epicTask.setStatus(Status.IN_PROGRESS);
            } else if (newCount == epicTask.getSubtasks().size()) {
                epicTask.setStatus(Status.NEW);
            } else if (doneCount == epicTask.getSubtasks().size()){
                epicTask.setStatus(Status.DONE);
            } else{
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


    public boolean checkId(int id) {
        if (tasks.containsKey(id)) {
            return true;
        } else return epicTasks.containsKey(id);
    }
}