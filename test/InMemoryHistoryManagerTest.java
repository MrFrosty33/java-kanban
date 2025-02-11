import controllers.InMemoryTaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class InMemoryHistoryManagerTest {
    static InMemoryTaskManager manager;
    static Task task1, task2;
    static Subtask subtask1, subtask2;
    static Epic epic1, epic2;

    @BeforeAll
    public static void beforeAll() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        Duration fiveHours = Duration.ofHours(5);
        LocalDateTime now = LocalDateTime.now();

        manager = new InMemoryTaskManager();

        task1 = new Task("task1", "id-1", Status.NEW, fiveHours, now);
        task2 = new Task("task2", "id-2", Status.IN_PROGRESS, fiveHours, yesterday);
        epic1 = new Epic("epic1", "id-3");
        epic2 = new Epic("epic2", "id-4");
        subtask1 = new Subtask("subtask1", "id-5", Status.NEW, 3);
        subtask2 = new Subtask("subtask2", "id-6", Status.IN_PROGRESS,
                fiveHours, yesterday.minusMonths(1), 4);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
    }

    @Test
    public void historyManagerCanAddTasksInHisHistoryListAngReturnThemByCall() {
        manager.getTask(1);
        manager.getTask(2);
        manager.getEpic(3);
        manager.getEpic(4);
        manager.getSubtask(6);
        manager.getEpic(4);
        manager.getSubtask(6);
        manager.getEpic(3);
        manager.getEpic(4);
        manager.getTask(1);

        System.out.println("Список истории просмотра задач" + manager.getHistory().getHistory());

        Assertions.assertNotNull(manager.getHistory().getHistory());
    }

    @Test
    public void historyManagerCanDeleteProperly() {
        manager.getTask(1);
        manager.getTask(2);
        manager.getEpic(3);
        manager.getEpic(4);
        manager.getSubtask(6);
        System.out.println("Список истории просмотра задач до удаления " + manager.getHistory().getHistory());

        manager.getHistory().remove(1);
        System.out.println("Список после удаления " + manager.getHistory().getHistory());

        Assertions.assertEquals(4, manager.getHistory().getHistory().size());

    }
}
