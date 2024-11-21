package tests;

import controllers.InMemoryTaskManager;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import utils.Managers;

public class ManagersTest {
    Managers<InMemoryTaskManager> managers;

    @Test
    public void managersReturnsInitializedObjects() {
        managers = new Managers<>();
        Assertions.assertNotNull(managers);
    }
}
