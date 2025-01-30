import controllers.InMemoryTaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.LocalDateTime;

public class InMemoryTaskManagerTest {

    @Test
    public void inMemoryTaskManagerCanAddDifferentTasksAndFindItById() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Duration hourThirtyMinutes = Duration.ofHours(1).plusMinutes(30);
        Duration fiveHours = Duration.ofHours(5);
        LocalDateTime longTimeAgo = LocalDateTime.now().minusYears(2).minusWeeks(35).minusMinutes(912);
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task("task1", "id-1", Status.NEW, hourThirtyMinutes, now);
        Task task2 = new Task("task2", "id-2", Status.NEW, fiveHours, yesterday);
        Subtask subtask1 = new Subtask("subtask1", "id-3", Status.NEW, fiveHours, longTimeAgo, 4);
        Subtask subtask2 = new Subtask("subtask2", "id-4", Status.NEW, hourThirtyMinutes, now, 4);
        Epic epic1 = new Epic("epic1", "id-5");
        Epic epic2 = new Epic("epic2", "id-6");

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
