package test;

import org.junit.jupiter.api.Test;
import tracker.InMemoryHistoryManager;
import tracker.Managers;
import tracker.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    public void getDefaultHistoryShouldReturnInitializedInstance() {
        InMemoryHistoryManager history1 = Managers.getDefaultHistory();
        InMemoryHistoryManager history2 = Managers.getDefaultHistory();

        assertNotNull(history1, "Первый экземпляр истории не должен быть null.");
        assertNotNull(history2, "Второй экземпляр истории не должен быть null.");
        assertEquals(history1, history2, "Оба экземпляра истории должны быть равны.");
    }

    @Test
    public void getDefaultShouldReturnInitializedInstance() {
        TaskManager default1 = Managers.getDefault();
        TaskManager default2 = Managers.getDefault();

        assertNotNull(default1, "Первый экземпляр TaskManager не должен быть null.");
        assertNotNull(default2, "Второй экземпляр TaskManager не должен быть null.");
        assertEquals(default1, default2, "Оба экземпляра TaskManager должны быть равны.");
    }
}