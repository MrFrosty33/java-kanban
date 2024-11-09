import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasks;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtasks(ArrayList<Integer> subtasks) {
        this.subtasks = subtasks;
    }
}
