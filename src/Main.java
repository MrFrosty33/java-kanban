import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = new Task("Задача 1", "Создать задачу 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Создать задачу 2", Status.NEW);

        Epic epic1 = new Epic("Эпик 1", "Описание первого эпика", Status.NEW);
        Epic epic2 = new Epic("Эпик 2", "Описание второго эпика", Status.NEW);

        Subtask subtask1 = new Subtask("1", "пусто", Status.NEW);
        Subtask subtask2 = new Subtask("2", "пусто", Status.IN_PROGRESS);
        Subtask subtask3 = new Subtask("3", "пусто", Status.DONE);

        manager.addTask(task1); //id 1
        manager.addTask(task2); //id 2
        manager.addEpic(epic1); //id 3
        manager.addEpic(epic2); //id 4
        manager.addSubtask(subtask1); //id 5
        manager.addSubtask(subtask2); //id 6
        manager.addSubtask(subtask3); //id 7

        System.out.println(manager.getEpic(3).getStatus()); // ожидаем NEW
        System.out.println(manager.getEpic(4).getStatus()); // ожидаем NEW

        // ID назначается перед помещением в мапу в менеджере. Чтобы у объектов здесь был ID
        // и они соответствовали уже помещённому в мапу объекту, выставляю вручную
        task1.setId(1);
        task1.setId(2);
        epic1.setId(3);
        epic2.setId(4);
        subtask1.setId(5);
        subtask1.setId(6);
        subtask1.setId(7);

        ArrayList<Integer> subtasks1 = new ArrayList<>();
        ArrayList<Integer> subtasks2 = new ArrayList<>();

        subtasks1.add(5);
        subtasks1.add(6);
        subtasks2.add(7);

        epic1.addSubtasks(subtasks1);
        epic2.addSubtasks(subtasks2);

        manager.updateEpic(epic1);
        manager.updateEpic(epic2);

        System.out.println(manager.getEpic(3).getStatus()); // ожидаем IN_PROGRESS
        System.out.println(manager.getEpic(4).getStatus()); // ожидаем DONE
    }
}
