package controllers;

import models.Epic;
import models.Status;
import models.Subtask;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        File path = new File("C:\\Programms\\IntelliJ Projects\\Files\\Sprint7\\Final\\sprint7.csv");
        InMemoryTaskManager manager = FileBackedTaskManager.loadFromFile(path, new InMemoryTaskManager());
        System.out.println("Прошла загрузка из файла: " + manager.toString());

        //InMemoryTaskManager manager = getManager();

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

        manager.getHistory().remove(1);

        System.out.println("Удалил Эпик." + manager.getHistory().getHistory());

        manager.getHistory().remove(4);

        System.out.println("Удалил Подзадачу." + manager.getHistory().getHistory());

        manager.getHistory().remove(2);

        System.out.println("Удалил эпик с 3-мя подзадачами." + manager.getHistory().getHistory());


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

        return manager;
    }
}
