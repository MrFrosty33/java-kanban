package controllers;

import models.Epic;
import models.Status;
import models.Subtask;

import java.io.File;

public class Main {
    private static final File path = new File("src/resources/sprint7.csv");

    public static void main(String[] args) {
        testLoadAndSave();
        //testSave(new File("src/resources/test.csv"));

    }

    private static InMemoryTaskManager getManager() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epicEmpty, epicWithThreeSubtasks;
        Subtask subtask1, subtask2, subtask3;


        epicEmpty = new Epic("epic1", "id-1");
        epicWithThreeSubtasks = new Epic("epic2", "id-2");
        subtask1 = new Subtask("subtask1", "id-3", Status.NEW, 2);
        subtask2 = new Subtask("subtask2", "id-4", Status.NEW, 2);
        subtask3 = new Subtask("subtask3", "id-5", Status.NEW, 2);

        manager.addEpic(epicEmpty);
        manager.addEpic(epicWithThreeSubtasks);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        manager.getEpic(1);
        manager.getSubtask(5);
        manager.getEpic(2);
        manager.getSubtask(3);
        manager.getSubtask(4);

        return manager;
    }

    private static void testSave() {
        InMemoryTaskManager manager = getManager();
        FileBackedTaskManager fileManager = new FileBackedTaskManager(path, manager);
        fileManager.save();
    }

    private static void testSave(File path){
        InMemoryTaskManager manager = getManager();
        FileBackedTaskManager fileManager = new FileBackedTaskManager(path, manager);
        fileManager.save();
    }

    private static void testLoadAndSave() {
        InMemoryTaskManager manager;

        if(path.isFile() && path.exists()){
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
