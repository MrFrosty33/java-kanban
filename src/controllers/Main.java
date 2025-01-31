package controllers;

import models.Epic;
import models.Status;
import models.Subtask;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Main {
    private static final File path = new File("src/resources/sprint7.csv");

    public static void main(String[] args) {
        testLoadAndSave();
        //testSave(new File("src/resources/test.csv"));


        /*
        InMemoryTaskManager manager = getManager();
        ArrayList<Subtask> subtasks = manager.getPrioritizedSubtasks();
        //ArrayList<Epic> epics = manager.getPrioritizedEpics();
        for (Subtask subtask : subtasks) {
            System.out.println(subtask);
        }

         */




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


        // Здесь тоже важно, чтобы эпик добавлялся раньше, чем его сабтаск
        epicEmpty = new Epic("epic1", "id-1");
        epicWithThreeSubtasks = new Epic("epic2", "id-2");
        epicNoTime = new Epic("epic1", "id-3");
        subtask1 = new Subtask("subtask1", "id-4", Status.NEW, hourThirtyMinutes, yesterday, 2);
        subtask2 = new Subtask("subtask2", "id-5", Status.NEW, fiveHours, now, 2);
        subtask3 = new Subtask("subtask3", "id-6", Status.NEW, fiveHours, longTimeAgo, 2);
        subtask4 = new Subtask("subtask3", "id-7", Status.IN_PROGRESS,3);

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
        //TODO добавить новые обхекты и в метод testLoadAndSave()

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
        manager.getSubtask(5);
        manager.getEpic(2);
        manager.getSubtask(3);
        manager.getSubtask(4);

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
