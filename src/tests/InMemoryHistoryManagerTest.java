package tests;

import controllers.InMemoryTaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

public class InMemoryHistoryManagerTest {
    static InMemoryTaskManager manager;
    static Task task1, task2;
    static Subtask subtask1, subtask2;
    static Epic epic1, epic2;

    @BeforeAll
    public static void beforeAll(){
        manager = new InMemoryTaskManager();
        task1 = new Task("test", "test", Status.NEW);
        task2 = new Task("test-1", "test-1", Status.NEW);
        subtask1 = new Subtask("test", "test", Status.NEW, 3);
        subtask2 = new Subtask("test-1", "test-1", Status.NEW, 4);
        epic1 = new Epic("test", "test");
        epic2 = new Epic("test-1", "test-1");

        manager.addTask(task1); //1
        manager.addTask(task2); //2
        manager.addEpic(epic1); //3
        manager.addEpic(epic2); //4
        manager.addSubtask(subtask1); //5
        manager.addSubtask(subtask2); //6

    }

    @Test
    public void HistoryManagerCanAddTasksInHisHistoryListAngReturnThemByCall(){
        manager.getTask(1);
        manager.getEpic(3);
        manager.getEpic(4);
        manager.getSubtask(6);
        manager.getEpic(4);
        manager.getEpic(4);
        manager.getEpic(4);
        manager.getEpic(4);
        manager.getEpic(4);
        manager.getEpic(4);
        manager.getSubtask(6);
        manager.getEpic(3);
        manager.getEpic(4);

        System.out.println(manager.getHistory().getHistory());

        Assertions.assertNotNull(manager.getHistory().getHistory());
    }
}
