import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    public void testGetDefaultHistoryReturnsInitializedInstance() {
        InMemoryHistoryManager history1 = Managers.getDefaultHistory();
        InMemoryHistoryManager history2 = Managers.getDefaultHistory();

        assertNotNull(history1);
        assertNotNull(history2);
        assertEquals(history1, history2);
    }

    @Test
    public void testGetDefaultReturnsInitializedInstance() {
        TaskManager default1 = Managers.getDefault();
        TaskManager default2 = Managers.getDefault();

        assertNotNull(default1);
        assertNotNull(default2);
        assertEquals(default1, default2);
    }
}