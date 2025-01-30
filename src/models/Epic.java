package models;

import java.time.Duration;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Epic extends Task{

    public ArrayList<Integer> subtasks;
    private Duration duration;
    private LocalDateTime startTime, endTime;
    //TODO высчитать время и длительность



    public Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
        // временно
        this.duration = Duration.ofMinutes(90);
        this.startTime = LocalDateTime.now();
        this.endTime = LocalDateTime.now().plus(duration);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtasks;
    }

    //TODO разобраться, зачем Override нужен тут вообще
    @Override
    public String toString() {
        // Длительность не стал форматировать, но при желании можно сделать
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy|HH:mm");
        String taskClass = this.getClass().getName().substring(7).toUpperCase();
        String separator = ",";
        // Всё это, чтобы убрать "class models.XXX" из названия класса
        String result = getId() + separator + taskClass + separator + getName() + separator
                + getStatus() + separator + getDescription() + separator
                + duration + separator + formatter.format(startTime)
                + separator + formatter.format(endTime) + separator;

        return result;
    }
}
