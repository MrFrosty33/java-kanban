import controllers.InMemoryTaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import java.time.Duration;
import java.time.LocalDateTime;

public class InMemoryTaskManagerTest {
    static InMemoryTaskManager manager;

    @BeforeAll
    public static void beforeAll() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        Duration fiveHours = Duration.ofHours(5);
        LocalDateTime now = LocalDateTime.now();

        manager = new InMemoryTaskManager();

        Task task1 = new Task("task1", "id-1", Status.NEW, fiveHours, now);
        Task task2 = new Task("task2", "id-2", Status.IN_PROGRESS, fiveHours, yesterday);
        Epic epic1 = new Epic("epic1", "id-3");
        Epic epic2 = new Epic("epic2", "id-4");
        Subtask subtask1 = new Subtask("subtask1", "id-5", Status.NEW, 3);
        Subtask subtask2 = new Subtask("subtask2", "id-6", Status.IN_PROGRESS,
                fiveHours, yesterday.minusMonths(1), 4);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
    }

    @Test
    public void inMemoryTaskManagerCanAddDifferentTasksAndFindItById() {
        Assertions.assertNotNull(manager.getTask(1));
        Assertions.assertNotNull(manager.getTask(2));
        Assertions.assertNotNull(manager.getSubtask(5));
        Assertions.assertNotNull(manager.getSubtask(6));
        Assertions.assertNotNull(manager.getEpic(3));
        Assertions.assertNotNull(manager.getEpic(4));
    }
}
