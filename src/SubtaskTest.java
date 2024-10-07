import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    String epicHeading = "second epic";
    String epicDescription = "second description";

    String subHeading = "second subtask";
    String subDescription = "second subtask description";
    int id;

    String subHeading_ = "third subtask";
    String subDescription_ = "third subtask description";

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    HashMap<Integer, ArrayList<Subtask>> subTasks = new HashMap<>();

    InMemoryTaskManager manager = new InMemoryTaskManager(tasks, epicTasks, subTasks);


    HashMap<Integer, EpicTask> epicTaskHashMap = new HashMap<>();

    @Test
    public void shouldGiveEpic() {
        id = manager.getTaskIndex(epicHeading, epicDescription, "epic task");
        EpicTask epic = new EpicTask(epicHeading, epicDescription, id);

        id = manager.getTaskIndex(subHeading, subDescription, "subtask");
        Subtask subtask = new Subtask(subHeading, subDescription, id, epic.id);

        epicTaskHashMap.put(epic.id, epic);

        Assertions.assertEquals(subtask.getEpicTask(epicTaskHashMap), epic);
    }

    @Test
    public void shouldGiveNullIfEpicIsNotInTheMap() {
        id = manager.getTaskIndex(epicHeading, epicDescription, "epic task");
        EpicTask epic = new EpicTask(epicHeading, epicDescription, id);

        id = manager.getTaskIndex(subHeading_, subDescription_, "subtask");
        Subtask subtaskSecond = new Subtask(subHeading_, subDescription_, id, 78);

        epicTaskHashMap.put(epic.id, epic);

        assertNull(subtaskSecond.getEpicTask(epicTaskHashMap));
    }
}