import java.util.ArrayList;

public class Epic extends Task {

    //todo * Завершение всех подзадач эпика считается завершением эпика.
    private ArrayList<Integer> subtasks;

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Integer> subtasks) {
        this.subtasks = subtasks;
    }
}
