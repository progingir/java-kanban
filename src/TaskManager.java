import java.util.*;
import java.util.ArrayList;

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

    public void getAllTasks() {
        for (Integer key : tasks.keySet()) {
            System.out.println(tasks.get(key).printTask());
        }
    }

    public void getAllEpicTasks() {
        for (Integer key : epicTasks.keySet()) {
            System.out.println(epicTasks.get(key).printTask());
        }
    }

    public void getAllSubTasks() {
        for (Integer key : subTasks.keySet()) {
            for (Subtask subtask : subTasks.get(key)) {
                System.out.println(subtask.printTask());
            }
        }
    }

    public void removeAllTasks() {
        tasks.clear();
        taskIndexMap.clear();
    }

    public void removeAllEpicTasks() {
        epicTasks.clear();
    }

    public void removeAllSubTasks() {
        subTasks.clear();
    }

    public String getTaskById(int id) {
        return tasks.get(id).printTask();
    }

    public String getEpicTaskById(int id) {
        return epicTasks.get(id).printTask();
    }

    public String getSubTaskById(int id) {
        for (List<Subtask> subtasks : subTasks.values()) {
            for (Subtask subtask : subtasks) {
                if (subtask.getId() == id) {
                    return subtask.printTask();
                }
            }
        }
        return ("Задачи с таким идентификатором пока нет");
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

    public void getSubtasks(int id) {
        if (epicTasks.containsKey(id)) {
            ArrayList<Subtask> subtaskArray = subTasks.get(id);
            if (subtaskArray == null || subtaskArray.isEmpty()) {
                System.out.println("У этого эпика пока что нет задач");
            } else {
                for (Subtask st : subtaskArray) {
                    System.out.println(st.printTask());
                }
            }
        } else {
            System.out.println("эпика с идентификатором " + id + " не существует");
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


    public void checkStatus(int id, int status) {
        if (tasks.containsKey(id)) {
            if (status == 1) {
                tasks.get(id).setStatus(Status.IN_PROGRESS);
            } else {
                tasks.get(id).setStatus(Status.DONE);
            }
            return;
        }

        for (List<Subtask> subtasks : subTasks.values()) {
            for (Subtask subtask : subtasks) {
                if (subtask.getId() == id) {
                    if (status == 1) {
                        subtask.setStatus(Status.IN_PROGRESS);
                    } else {
                        subtask.setStatus(Status.DONE);
                    }
                }
            }
        }
    }

    public boolean checkId(int id) {
        if (tasks.containsKey(id)) {
            return true;
        } else return epicTasks.containsKey(id);
    }
}
