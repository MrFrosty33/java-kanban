package controllers;

import model.*;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Создать задачу 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Создать задачу 2", Status.NEW);

        Epic epic1 = new Epic("Эпик 1", "Описание первого эпика");
        Epic epic2 = new Epic("Эпик 2", "Описание второго эпика");

        Subtask subtask1 = new Subtask("1", "пусто", Status.NEW, 3);
        Subtask subtask2 = new Subtask("2", "пусто", Status.IN_PROGRESS, 3);
        Subtask subtask3 = new Subtask("3", "пусто", Status.DONE, 4);

        manager.addTask(task1); //id 1
        manager.addTask(task2); //id 2
        manager.addEpic(epic1); //id 3
        manager.addEpic(epic2); //id 4

        System.out.println(manager.getEpic(3).getStatus()); // ожидаем NEW
        System.out.println(manager.getEpic(4).getStatus()); // ожидаем NEW

        manager.addSubtask(subtask1); //id 5
        manager.addSubtask(subtask2); //id 6
        manager.addSubtask(subtask3); //id 7

        System.out.println(manager.getEpic(3).getStatus()); // ожидаем IN_PROGRESS
        System.out.println(manager.getEpic(4).getStatus()); // ожидаем DONE
    }
}
