package models;

public class Subtask extends Task {

    private int epicId;


    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString(){
        return super.toString() + getEpicId() + ",";
    }
}
