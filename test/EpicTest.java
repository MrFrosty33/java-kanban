import controllers.InMemoryTaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class EpicTest {
    static InMemoryTaskManager manager;
    static Subtask subtask1, subtask2, subtask3, subtask4, subtask5;
    static Epic epicNew, epicDone, epicInProgress, epicMixed;

    @BeforeAll
    public static void beforeAll() {
        Duration hourThirtyMinutes = Duration.ofHours(1).plusMinutes(30);
        Duration fiveHours = Duration.ofHours(5);
        LocalDateTime longTimeAgo = LocalDateTime.now().minusYears(2).minusWeeks(35).minusMinutes(912);
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime now = LocalDateTime.now();
        manager = new InMemoryTaskManager();

        epicNew = new Epic("epicNew", "id-1");
        epicDone = new Epic("epicDone", "id-2");
        epicInProgress = new Epic("epicInProgress", "id-3");
        epicMixed = new Epic("epicMixed", "id-4");
        subtask1 = new Subtask("subtask1", "id-5", Status.NEW, fiveHours, longTimeAgo, 1);
        subtask2 = new Subtask("subtask2", "id-6", Status.DONE, hourThirtyMinutes, now, 2);
        subtask3 = new Subtask("subtask3", "id-7", Status.IN_PROGRESS, hourThirtyMinutes, now, 3);
        subtask4 = new Subtask("subtask4", "id-8", Status.NEW, fiveHours, longTimeAgo, 4);
        subtask5 = new Subtask("subtask5", "id-9", Status.DONE, hourThirtyMinutes, now, 4);

        manager.addEpic(epicNew);
        manager.addEpic(epicDone);
        manager.addEpic(epicInProgress);
        manager.addEpic(epicMixed);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        manager.addSubtask(subtask4);
        manager.addSubtask(subtask5);
    }

    @Test
    public void epicHasStatusNew() {
        Assertions.assertEquals(manager.getEpic(1).getStatus(), Status.NEW);
    }

    @Test
    public void epicHasStatusDone() {
        Assertions.assertEquals(manager.getEpic(2).getStatus(), Status.DONE);
    }

    @Test
    public void epicHasStatusInProgress() {
        Assertions.assertEquals(manager.getEpic(3).getStatus(), Status.IN_PROGRESS);
        Assertions.assertEquals(manager.getEpic(4).getStatus(), Status.IN_PROGRESS);
        // если сабтаски со статусом NEW и DONE должен же быть статус эпика IN_PROGRESS?
    }
}
