package model;

import java.util.ArrayList;

public class Epic extends Task {

    public ArrayList<Integer> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtasks;
    }

    /*

    Пока закомментирую, потом сотру. Правильно понял, что этот метод является тофтологией, и можно сделать список публичным
    и добавлять подзадачи через subtasks.add(); ?
    public void addSubtasks(ArrayList<Integer> subtasks) {
        this.subtasks = subtasks;
    }
     */
}
