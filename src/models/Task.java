package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

    private int id;
    private String name;
    private String description;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime, endTime;


    /**
     * ----- Constructors -----
     */

    public Task(String name, String description, Status status,
                Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = getEndTime();
    }


    public Task(String name, String description) {
        // Этот конструктор применяется только Эпиком
        this.name = name;
        this.description = description;
    }

    /**
     * ----- Getter-Setter -----
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return LocalDateTime.from(startTime.plus(duration));
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = getEndTime();
    }

    /**
     * ----- @Override -----
     */

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
