package models;

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
}
