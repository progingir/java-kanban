package tracker;

import java.util.HashMap;
import java.util.List;

public interface Manager {

    Task addTask(Task task);

    Task getTaskById(int id);

    void deleteTasks();

    HashMap<Integer, Task> getTasks();

    void updateTask(Task task);

    void deleteTaskById(int id);

    EpicTask addEpic(EpicTask epic);

    EpicTask getEpicById(int id);

    void deleteEpics();

    HashMap<Integer, EpicTask> getEpics();

    void updateEpic(EpicTask epic);

    void deleteEpicById(int id);

    Subtask addSubtask(Subtask subtask);

    Subtask getSubtaskById(int id);

    void deleteSubtasks();

    HashMap<Integer, Subtask> getSubtasks();

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    List<Subtask> getSubtasksOfEpic(int id);
}
