import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        //todo Тестирование всего и вся!!!

        TaskManager manager = new TaskManager();

        Epic e1 = new Epic("Тестовый", "Все NEW", Status.DONE);
        Subtask s1 = new Subtask("1", "none", Status.NEW);
        Subtask s2 = new Subtask("2", "none", Status.NEW);
        Subtask s3 = new Subtask("3", "none", Status.NEW);

        manager.addEpic(e1);

        manager.addSubtask(s1);
        manager.addSubtask(s2);
        manager.addSubtask(s3);

        ArrayList<Integer> subtasks = new ArrayList<>();
        subtasks.add(1);
        subtasks.add(2);
        subtasks.add(3);

        e1.addSubtasks(subtasks);

        manager.epicCorrectStatus(e1); // nullPointerException. Перепроверить
        System.out.println(e1.getStatus());
    }
}
