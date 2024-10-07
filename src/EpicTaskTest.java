import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;

class EpicTaskTest {
    String epicHeading = "first epic";
    String epicDescription = "first description";

    String subHeading = "first subtask";
    String subDescription = "first subtask description";
    int id;

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    HashMap<Integer, ArrayList<Subtask>> subTasks = new HashMap<>();

    InMemoryTaskManager manager = new InMemoryTaskManager(tasks, epicTasks, subTasks);

    @Test
    public void shouldGiveListOfSubtasks() {
        id = manager.getTaskIndex(epicHeading,epicDescription, "epic task");
        EpicTask epicTask = new EpicTask(epicHeading, epicDescription, id);
        id = manager.getTaskIndex(subHeading,subDescription, "subtask");
        Subtask subtask = new Subtask(subHeading, subDescription, id, epicTask.id);

        ArrayList<Subtask> expectedSubtasks = new ArrayList<>();
        expectedSubtasks.add(subtask);
        epicTask.addSubtask(subtask);

        Assertions.assertEquals(expectedSubtasks, epicTask.getSubtasks());
    }
}