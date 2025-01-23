package controllers;

import interfaces.HistoryManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;

import java.io.*;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private File myFile;
    private HistoryManager history;

    public FileBackedTaskManager(File myFile, HistoryManager history) {
        //TODO убрать этот конструктор
        // почему-то конструкторы работают только если передавать историю.
        // Если брать дефолтную, по факту ту же самую - не работает
        this.myFile = myFile;
        this.history = history;
    }

    public FileBackedTaskManager(File myFile) {
        this.myFile = myFile;
        this.history = super.getHistory();
    }

    public void save() {
        // Пример сохранения в CSV: id,type,name,status,description,epic
        try (FileWriter fw = new FileWriter(myFile); BufferedWriter bw = new BufferedWriter(fw)) {
            String title = "id,type,name,status,description,epic" + "\n";
            String separator = ",";
            bw.write(title);

            for (Task task : history.getHistory()) {
                String toWrite;
                String taskClass = task.getClass().getName().substring(7).toUpperCase();
                // Всё это, чтобы убрать "class models.XXX" из названия класса

                toWrite = task.getId() + separator;
                toWrite += taskClass + separator + task.getName() + separator +
                        task.getStatus() + separator + task.getDescription() + separator;

                if (task.getClass().equals(Subtask.class)) {
                    Subtask subtask = (Subtask) task;
                    toWrite += subtask.getEpicId() + separator;
                }

                bw.write(toWrite + "\n");
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        // TODO на данный момент не работает
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
            br.readLine(); // пропускаем первую стоку с оглавлением


            while (br.ready()) {
                String next = br.readLine();
                String[] nextLine = next.split(",");
                //StringBuilder nextLineBuilder = new StringBuilder(next);
                Status status;

                if (nextLine[3].equals("NEW")) {
                    status = Status.NEW;
                } else if (nextLine[3].equals("DONE")) {
                    status = Status.DONE;
                } else {
                    status = Status.IN_PROGRESS;
                }

                if (nextLine[1].equals("SUBTASK")) {
                    Subtask subtask = new Subtask(nextLine[2], nextLine[4], status, Integer.valueOf(nextLine[5]));
                    //name,      descr,    status,       epic id
                    fileBackedTaskManager.history.add(subtask);
                } else if (nextLine[1].equals("EPIC")) {
                    Epic epic = new Epic(nextLine[2], nextLine[4]);
                    //name,      descr
                    fileBackedTaskManager.history.add(epic);
                } else {
                    Task task = new Task(nextLine[2], nextLine[4]);
                    //name,      descr
                    fileBackedTaskManager.history.add(task);
                }
            }

            return fileBackedTaskManager;
        } catch (Throwable ex) {
            ex.printStackTrace();
            return fileBackedTaskManager;
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }
}