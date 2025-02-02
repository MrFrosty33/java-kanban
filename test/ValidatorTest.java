import controllers.InMemoryTaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import validators.TaskValidator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ValidatorTest {
    static InMemoryTaskManager manager;
    static Task task1, task2, task3;
    static Subtask subtask1, subtask2, subtask3;

    @BeforeAll
    public static void beforeAll() {
        manager = new InMemoryTaskManager();
        Duration fiveHours = Duration.ofHours(5);
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime now = LocalDateTime.now();
        task1 = new Task("task1", "id-1", Status.NEW);
        task2 = new Task("task2", "id-2", Status.NEW, fiveHours, now);
        task3 = new Task("task3", "id-3", Status.NEW, fiveHours, yesterday);
        subtask1 = new Subtask("subtask1", "id-6", Status.NEW, 5);
        subtask2 = new Subtask("subtask2", "id-7", Status.NEW, fiveHours, now, 4);
        subtask3 = new Subtask("subtask3", "id-8", Status.NEW, fiveHours, yesterday, 4);
        Epic epic1 = new Epic("epic1", "id-4");
        Epic epic2 = new Epic("epic2", "id-5");

        manager.addTask(task1); //1
        manager.addTask(task2); //2
        manager.addTask(task3); //3
        manager.addEpic(epic1); //4
        manager.addEpic(epic2); //5
        manager.addSubtask(subtask1); //6
        manager.addSubtask(subtask2); //7
        manager.addSubtask(subtask3); //8
    }

    @Test
    public void validatorReturnsTrue() {
        //true = есть наложение
        Assertions.assertTrue(TaskValidator.validateTime(task2, task3));
        Assertions.assertTrue(TaskValidator.validateTime(subtask2, subtask3));
    }

    @Test
    public void validatorReturnsFalse() {
        //false = всё в порядке
        // По условиям, если нет времени, значит и не добавляется в дерево по приоритету
        // поэтому валидатор не учитывает варианта, когда ему передают класс без startTime
        // и закомментированный тест ниже не требуется
        //Assertions.assertFalse(TaskValidator.validateTime(task1, task2));
        Assertions.assertFalse(TaskValidator.validateTime(subtask3, subtask2));
    }
}
