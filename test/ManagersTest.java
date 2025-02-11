import interfaces.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utils.Managers;

public class ManagersTest {
    TaskManager taskManager;

    @Test
    public void managersReturnsInitializedObjects() {
        taskManager = Managers.getDefault();
        Assertions.assertNotNull(taskManager);
    }
}
