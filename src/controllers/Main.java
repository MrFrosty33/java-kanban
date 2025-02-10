package controllers;

import API.HttpTaskServer;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;


public class Main {
    private static final File path = new File("src/resources/sprint7.csv");

    public static void main(String[] args) {
        //testLoadAndSave();
        //testSave(new File("src/resources/test.csv"));


        /*
        InMemoryTaskManager manager = getManager();
        ArrayList<Subtask> subtasks = manager.getPrioritizedSubtasks();
        //ArrayList<Epic> epics = manager.getPrioritizedEpics();
        for (Subtask subtask : subtasks) {
            System.out.println(subtask);
        }
         */

        testGetTasksHandler();


    }

    private static void testGetTasksHandler() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1, task2, task3;
        Epic epic1, epic2;
        Subtask subtask1, subtask2, subtask3;
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        Duration fiveHours = Duration.ofHours(5);
        LocalDateTime now = LocalDateTime.now();

        task1 = new Task("task1", "id-1", Status.NEW, fiveHours, now);
        task2 = new Task("task2", "id-2", Status.IN_PROGRESS, fiveHours, yesterday);
        task3 = new Task("task3", "id-3", Status.NEW);
        epic1 = new Epic("epic1", "id-4");
        epic2 = new Epic("epic2", "id-5");
        subtask1 = new Subtask("subtask1", "id-6", Status.NEW, 4);
        subtask2 = new Subtask("subtask2", "id-7", Status.IN_PROGRESS,
                fiveHours, yesterday.minusMonths(1), 5);
        subtask3 = new Subtask("subtask2", "id-8", Status.NEW,
                fiveHours, yesterday.minusYears(1).plusWeeks(1), 5);


        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        manager.getTask(1);
        manager.getTask(2);
        manager.getTask(3);
        manager.getEpic(4);
        manager.getEpic(5);
        manager.getSubtask(6);
        manager.getSubtask(7);
        manager.getSubtask(8);

        System.out.println(manager.getHistory().getHistory());
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
    }

    private static InMemoryTaskManager getManager() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epicEmpty, epicWithThreeSubtasks, epicNoTime;
        Subtask subtask1, subtask2, subtask3, subtask4;
        Duration hourThirtyMinutes = Duration.ofHours(1).plusMinutes(30);
        Duration fiveHours = Duration.ofHours(5);
        LocalDateTime longTimeAgo = LocalDateTime.now().minusYears(2).minusWeeks(35).minusMinutes(912);
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime now = LocalDateTime.now();


        epicEmpty = new Epic("epic1", "id-1");
        epicWithThreeSubtasks = new Epic("epic2", "id-2");
        epicNoTime = new Epic("epic1", "id-3");
        subtask1 = new Subtask("subtask1", "id-4", Status.NEW, hourThirtyMinutes, yesterday, 2);
        subtask2 = new Subtask("subtask2", "id-5", Status.NEW, fiveHours, now, 2);
        subtask3 = new Subtask("subtask3", "id-6", Status.NEW, fiveHours, longTimeAgo, 2);
        subtask4 = new Subtask("subtask3", "id-7", Status.IN_PROGRESS, 3);

        // Важно, чтобы эпик добавлялся раньше, чем его сабтаск
        manager.addEpic(epicEmpty);
        manager.addEpic(epicWithThreeSubtasks);
        manager.addEpic(epicNoTime);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        manager.addSubtask(subtask4);

        manager.getEpic(1);
        manager.getSubtask(6);
        manager.getEpic(2);
        manager.getSubtask(4);
        manager.getSubtask(5);
        manager.getEpic(3);
        manager.getSubtask(7);

        return manager;
    }

    private static void testSave() {
        InMemoryTaskManager manager = getManager();
        FileBackedTaskManager fileManager = new FileBackedTaskManager(path, manager);
        fileManager.save();
    }

    private static void testSave(File path) {
        InMemoryTaskManager manager = getManager();
        FileBackedTaskManager fileManager = new FileBackedTaskManager(path, manager);
        fileManager.save();
    }

    private static void testLoadAndSave() {
        InMemoryTaskManager manager;

        if (path.isFile() && path.exists()) {
            manager = FileBackedTaskManager.loadFromFile(path, new InMemoryTaskManager());
            System.out.println("Прошла загрузка из файла: " + manager.toString());
        } else {
            manager = getManager();
        }


        FileBackedTaskManager fileManager = new FileBackedTaskManager(path, manager);

        manager.getEpic(1);
        manager.getEpic(2);
        manager.getEpic(3);
        manager.getSubtask(4);
        manager.getSubtask(5);
        manager.getSubtask(6);
        manager.getSubtask(7);

        System.out.println("Список истории просмотра задач" + manager.getHistory().getHistory());

        manager.getEpic(1);
        manager.getEpic(2);

        System.out.println("Список истории просмотра задач" + manager.getHistory().getHistory());

        manager.getEpic(2);
        manager.getSubtask(4);

        System.out.println("Список истории просмотра задач" + manager.getHistory().getHistory());
        fileManager.save();
    }
}
