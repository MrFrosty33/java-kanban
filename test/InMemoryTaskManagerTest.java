import controllers.InMemoryTaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class InMemoryTaskManagerTest {

    @Test
    public void inMemoryTaskManagerCanAddDifferentTasksAndFindItById() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("test", "test", Status.NEW);
        Task task2 = new Task("test-1", "test-1", Status.NEW);
        Subtask subtask1 = new Subtask("test", "test", Status.NEW, 3);
        Subtask subtask2 = new Subtask("test-1", "test-1", Status.NEW, 4);
        Epic epic1 = new Epic("test", "test");
        Epic epic2 = new Epic("test-1", "test-1");

        manager.addTask(task1); //1
        manager.addTask(task2); //2
        manager.addEpic(epic1); //3
        manager.addEpic(epic2); //4
        manager.addSubtask(subtask1); //5
        manager.addSubtask(subtask2); //6


        Assertions.assertNotNull(manager.getTask(1));
        Assertions.assertNotNull(manager.getTask(2));
        Assertions.assertNotNull(manager.getSubtask(5));
        Assertions.assertNotNull(manager.getSubtask(6));
        Assertions.assertNotNull(manager.getEpic(3));
        Assertions.assertNotNull(manager.getEpic(4));
    }
}
