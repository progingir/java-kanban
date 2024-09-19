import java.util.*;
import java.util.ArrayList;

public class TaskManager {
    HashMap<Integer, Task> tasks;
    HashMap<Integer, EpicTask> epicTasks;
    HashMap<Integer, ArrayList<Subtask>> subTasks;

    Scanner scanner = new Scanner(System.in);
    int id = 0;

    Map<String, Integer> taskIndexMap = new HashMap<>();

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
        for (Integer key : epicTasks.keySet()) {
            System.out.println(epicTasks.get(key).printTask());
        }
    }

    public void removeAllTasks() {
        tasks.clear();
        epicTasks.clear();
        subTasks.clear();
        taskIndexMap.clear();
    }

    public String getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id).printTask();
        }

        if (epicTasks.containsKey(id)) {
            return epicTasks.get(id).printTask();
        }

        for (List<Subtask> subtasks : subTasks.values()) {
            for (Subtask subtask : subtasks) {
                if (subtask.getId() == id) {
                    return subtask.printTask();
                }
            }
        }

        return ("Задачи под этим индексом пока нет");
    }

    private int getTaskIndex(String heading, String description) {
        String taskKey = heading + " " + description;
        if (taskIndexMap.containsKey(taskKey)) {
            return taskIndexMap.get(taskKey);
        } else {
            return -1;
        }
    }

    public Task createTask(String heading, String description) {
        int index = getTaskIndex(heading, description);
        if (index != -1) {
            return tasks.get(index);
        } else {
            Task task = new Task(heading, description, ++id);
            task.setStatus(Status.NEW);
            tasks.put(id, task);
            taskIndexMap.put(heading + " " + description, id);
            return task;
        }
    }

    public Subtask createSubTask(String heading, String description, Integer epicTaskId) {
        int index = getTaskIndex(heading, description);
        if (index != -1) {
            for (Subtask subtask : subTasks.getOrDefault(epicTaskId, new ArrayList<>())) {
                if (subtask.getId() == index) {
                    return subtask;
                }
            }
        } else {
            Subtask subTask = new Subtask(heading, description, ++id);
            if (subTasks.containsKey(epicTaskId)) {
                subTasks.get(epicTaskId).add(subTask);
            } else {
                ArrayList<Subtask> subtaskArray = new ArrayList<>();
                subtaskArray.add(subTask);
                subTasks.put(epicTaskId, subtaskArray);
            }
            subTask.setStatus(Status.NEW);
            taskIndexMap.put(heading + " " + description, id);
            return subTask;
        }
        return null;
    }

    public EpicTask createEpicTask(String heading, String description) {
        int index = getTaskIndex(heading, description);
        if (index != -1) {
            return epicTasks.get(index);
        } else {
            EpicTask epicTask = new EpicTask(heading, description, ++id);
            epicTask.setStatus(Status.NEW);
            epicTasks.put(id, epicTask);
            taskIndexMap.put(heading + " " + description, id);
            return epicTask;
        }
    }

    public void updateTask(int id, int comm) {
        if (comm == 1) {
            System.out.println("Введите новое название:");
            String newHeading = scanner.next();
            tasks.get(id).setHeading(newHeading);
            System.out.println("Новое название сохранено!");
        } else if (comm == 2) {
            System.out.println("Введите новое описание:");
            String newDescription = scanner.next();
            tasks.get(id).setDescription(newDescription);
            System.out.println("Новое описание сохранено!");
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
            System.out.println("эпика с таким " + id + " не существует");
        }
    }

    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            String taskKey = tasks.get(id).heading + " " + tasks.get(id).description;
            taskIndexMap.remove(taskKey);
            tasks.remove(id);
        } else if (epicTasks.containsKey(id)) {
            String taskKey = epicTasks.get(id).heading + " " + epicTasks.get(id).description;
            taskIndexMap.remove(taskKey);
            epicTasks.remove(id);
        } else if (subTasks.containsKey(id)) {
            for (Subtask subtask : subTasks.get(id)) {
                String taskKey = subtask.heading + " " + subtask.description;
                taskIndexMap.remove(taskKey);
            }
            subTasks.remove(id);
        } else {
            System.out.println("Задачи с таким индексом нет!");
        }
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
}