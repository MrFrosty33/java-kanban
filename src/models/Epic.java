package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    public ArrayList<Integer> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    public Epic(String name, String description,
                Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(name, description, duration, startTime, endTime);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtasks;
    }
}
